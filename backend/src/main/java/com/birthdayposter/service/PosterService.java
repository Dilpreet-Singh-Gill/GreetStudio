package com.birthdayposter.service;

import com.birthdayposter.dto.GenerationHistoryResponse;
import com.birthdayposter.dto.PagedResponse;
import com.birthdayposter.entity.GenerationHistory;
import com.birthdayposter.entity.Person;
import com.birthdayposter.entity.Template;
import com.birthdayposter.entity.User;
import com.birthdayposter.exception.BadRequestException;
import com.birthdayposter.exception.ResourceNotFoundException;
import com.birthdayposter.repository.GenerationHistoryRepository;
import com.birthdayposter.repository.PersonRepository;
import com.birthdayposter.repository.TemplateRepository;
import com.birthdayposter.repository.UserRepository;
import com.birthdayposter.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosterService {

    private final PersonRepository personRepository;
    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final GenerationHistoryRepository historyRepository;
    private final HuggingFaceService huggingFaceService;
    private final ImageProcessingService imageProcessingService;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;

    private static final String POSTER_FOLDER = "greet-studio/posters";

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    public GenerationHistoryResponse generatePoster(Long personId, Long templateId) {
        Long userId = getCurrentUserId();
        Person person = personRepository.findByIdAndUserId(personId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        Template template;
        if (templateId != null) {
            template = templateRepository.findByIdAndUserId(templateId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        } else {
            // Pick a random template for the user
            Page<Template> templates = templateRepository.findByUserId(userId, PageRequest.of(0, 100));
            if (templates.isEmpty()) {
                throw new BadRequestException("No templates found. Please upload a template first.");
            }
            List<Template> templateList = templates.getContent();
            template = templateList.get(new Random().nextInt(templateList.size()));
        }

        return processPosterGeneration(person, template, person.getUser(), null);
    }

    /**
     * Core poster generation logic.
     *
     * @param person       the birthday person
     * @param template     the poster template to use
     * @param user         the app user (account holder)
     * @param imageBytesOut optional list to collect generated image bytes for batch email
     * @return the generation history response
     */
    private GenerationHistoryResponse processPosterGeneration(Person person, Template template, User user,
                                                               List<byte[]> imageBytesOut) {
        GenerationHistory history = GenerationHistory.builder()
                .person(person)
                .template(template)
                .user(user)
                .status("PROCESSING")
                .posterUrl("")
                .build();
        
        history = historyRepository.save(history);

        try {
            // 1. Generate Wish
            String wishText = huggingFaceService.generateBirthdayWish(person);
            history.setWishText(wishText);

            // 2. Process Image
            byte[] imageBytes = imageProcessingService.generatePoster(template, person, wishText);

            // 3. Upload to Cloudinary
            Map<String, String> uploadResult = cloudinaryService.uploadImage(imageBytes, POSTER_FOLDER);

            // 4. Save History
            history.setPosterUrl(uploadResult.get("url"));
            history.setPosterPublicId(uploadResult.get("public_id"));
            history.setStatus("SUCCESS");
            history = historyRepository.save(history);

            // 5. Collect image bytes for batch email, or send individual email
            if (imageBytesOut != null) {
                imageBytesOut.add(imageBytes);
            } else {
                // Manual generation — send individual email immediately
                emailService.sendPosterEmail(user, person, history, imageBytes);
            }

            return mapToResponse(history);

        } catch (Exception e) {
            log.error("Failed to generate poster for person ID: {}", person.getId(), e);
            history.setStatus("FAILED");
            history.setErrorMessage(e.getMessage());
            historyRepository.save(history);
            throw new BadRequestException("Poster generation failed: " + e.getMessage());
        }
    }

    public PagedResponse<GenerationHistoryResponse> getPosterHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GenerationHistory> historyPage = historyRepository.findByUserId(getCurrentUserId(), pageable);

        List<GenerationHistoryResponse> content = historyPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                historyPage.getNumber(),
                historyPage.getSize(),
                historyPage.getTotalElements(),
                historyPage.getTotalPages(),
                historyPage.isLast()
        );
    }

    /**
     * Daily automation: generate posters for today's birthdays across all users,
     * then send a digest email per user with all posters attached.
     */
    public void runDailyAutomation() {
        log.info("===== Running daily poster automation =====");
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        List<User> users = userRepository.findAll();
        int totalGenerated = 0;
        int totalFailed = 0;

        for (User user : users) {
            Page<Person> peoplePage = personRepository.findByUserId(user.getId(), PageRequest.of(0, 1000));
            List<Person> birthdaysToday = peoplePage.getContent().stream()
                    .filter(p -> p.getDob() != null && p.getDob().getMonthValue() == month && p.getDob().getDayOfMonth() == day)
                    .collect(Collectors.toList());

            if (birthdaysToday.isEmpty()) {
                log.info("No birthdays today for user: {}", user.getEmail());
                continue;
            }

            Page<Template> templates = templateRepository.findByUserId(user.getId(), PageRequest.of(0, 100));
            if (templates.isEmpty()) {
                log.warn("No templates found for user: {}. Skipping.", user.getEmail());
                continue;
            }

            List<Template> templateList = templates.getContent();
            Random rand = new Random();
            List<GenerationHistory> todayHistories = new ArrayList<>();

            for (Person person : birthdaysToday) {
                try {
                    Template template = templateList.get(rand.nextInt(templateList.size()));
                    processPosterGeneration(person, template, user, null);

                    // Fetch the latest history for this person (just created)
                    List<GenerationHistory> recent = historyRepository.findByUserId(user.getId(),
                            PageRequest.of(0, 1, Sort.by("createdAt").descending())).getContent();
                    if (!recent.isEmpty()) {
                        todayHistories.add(recent.get(0));
                    }

                    totalGenerated++;
                    log.info("Successfully generated automated poster for: {}", person.getName());
                } catch (Exception e) {
                    totalFailed++;
                    log.error("Failed automated poster for: {}", person.getName(), e);
                }
            }

            // Send daily digest email with all today's posters
            if (!todayHistories.isEmpty()) {
                try {
                    emailService.sendDailyDigestEmail(user, todayHistories);
                    log.info("Daily digest email sent to {}", user.getEmail());
                } catch (Exception e) {
                    log.error("Failed to send daily digest email to {}", user.getEmail(), e);
                }
            }
        }

        log.info("===== Daily automation complete: {} generated, {} failed =====", totalGenerated, totalFailed);
    }

    private GenerationHistoryResponse mapToResponse(GenerationHistory history) {
        GenerationHistoryResponse response = new GenerationHistoryResponse();
        response.setId(history.getId());
        response.setPersonId(history.getPerson().getId());
        response.setPersonName(history.getPerson().getName());
        response.setPosterUrl(history.getPosterUrl());
        response.setWishText(history.getWishText());
        response.setStatus(history.getStatus());
        response.setCreatedAt(history.getCreatedAt());
        return response;
    }
}

package com.birthdayposter.service;

import com.birthdayposter.dto.DashboardResponse;
import com.birthdayposter.entity.GenerationHistory;
import com.birthdayposter.entity.Person;
import com.birthdayposter.entity.User;
import com.birthdayposter.repository.GenerationHistoryRepository;
import com.birthdayposter.repository.PersonRepository;
import com.birthdayposter.repository.TemplateRepository;
import com.birthdayposter.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PersonRepository personRepository;
    private final TemplateRepository templateRepository;
    private final GenerationHistoryRepository historyRepository;

    public DashboardResponse getDashboardStats() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        LocalDate today = LocalDate.now();

        long totalPeople = personRepository.countByUserId(userId);
        long activeTemplates = templateRepository.countByUserId(userId);
        long postersGenerated = historyRepository.countByUserId(userId);
        
        List<Person> birthdaysTodayList = personRepository.findByUserIdAndBirthMonthAndDay(userId, today.getMonthValue(), today.getDayOfMonth());
        long birthdaysToday = birthdaysTodayList.size();

        // Get all people to calculate upcoming birthdays
        List<Person> allPeople = personRepository.findByUserId(userId);
        
        List<DashboardResponse.UpcomingBirthdayDto> upcomingBirthdays = allPeople.stream()
                .filter(p -> p.getDob() != null)
                .map(p -> new UpcomingBirthdayWrapper(p, calculateDaysUntilNextBirthday(p.getDob(), today)))
                .filter(w -> w.daysUntil >= 0) // Only future or today
                .sorted(Comparator.comparingInt(w -> w.daysUntil))
                .limit(3)
                .map(w -> mapToUpcomingBirthdayDto(w.person, w.daysUntil))
                .collect(Collectors.toList());

        List<GenerationHistory> recentHistory = historyRepository.findTop3ByUserIdOrderByCreatedAtDesc(userId);
        List<DashboardResponse.RecentPosterDto> recentPosters = recentHistory.stream()
                .map(this::mapToRecentPosterDto)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalPeople(totalPeople)
                .birthdaysToday(birthdaysToday)
                .activeTemplates(activeTemplates)
                .postersGenerated(postersGenerated)
                .upcomingBirthdays(upcomingBirthdays)
                .recentPosters(recentPosters)
                .build();
    }

    private int calculateDaysUntilNextBirthday(LocalDate dob, LocalDate today) {
        LocalDate nextBirthday = dob.withYear(today.getYear());
        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        return (int) ChronoUnit.DAYS.between(today, nextBirthday);
    }

    private DashboardResponse.UpcomingBirthdayDto mapToUpcomingBirthdayDto(Person p, int daysUntil) {
        String daysText;
        if (daysUntil == 0) daysText = "Today";
        else if (daysUntil == 1) daysText = "Tomorrow";
        else daysText = "In " + daysUntil + " days";

        String initials = p.getName().length() > 0 ? p.getName().substring(0, 1).toUpperCase() : "?";
        if (p.getName().contains(" ")) {
            String[] parts = p.getName().split(" ");
            if (parts.length > 1 && parts[1].length() > 0) {
                initials += parts[1].substring(0, 1).toUpperCase();
            }
        }

        return DashboardResponse.UpcomingBirthdayDto.builder()
                .id(p.getId())
                .name(p.getName())
                .initials(initials)
                .department(p.getDepartment() != null ? p.getDepartment() : "No Department")
                .dob(p.getDob().getMonth().name().substring(0, 3) + " " + p.getDob().getDayOfMonth())
                .daysUntilText(daysText)
                .build();
    }

    private DashboardResponse.RecentPosterDto mapToRecentPosterDto(GenerationHistory h) {
        long minutesAgo = ChronoUnit.MINUTES.between(h.getCreatedAt(), LocalDateTime.now());
        String timeText;
        if (minutesAgo < 60) timeText = minutesAgo + "m ago";
        else if (minutesAgo < 24 * 60) timeText = (minutesAgo / 60) + "h ago";
        else timeText = (minutesAgo / (24 * 60)) + "d ago";

        return DashboardResponse.RecentPosterDto.builder()
                .id(h.getId())
                .personName(h.getPerson().getName())
                .timeAgoText(timeText)
                .posterUrl(h.getPosterUrl())
                .build();
    }

    private static class UpcomingBirthdayWrapper {
        Person person;
        int daysUntil;
        UpcomingBirthdayWrapper(Person p, int daysUntil) {
            this.person = p;
            this.daysUntil = daysUntil;
        }
    }
}

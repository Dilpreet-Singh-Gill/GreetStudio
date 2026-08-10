package com.birthdayposter.service;

import com.birthdayposter.dto.PagedResponse;
import com.birthdayposter.dto.PersonRequest;
import com.birthdayposter.dto.PersonResponse;
import com.birthdayposter.entity.Person;
import com.birthdayposter.entity.User;
import com.birthdayposter.exception.ResourceNotFoundException;
import com.birthdayposter.repository.PersonRepository;
import com.birthdayposter.repository.UserRepository;
import com.birthdayposter.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final ExcelService excelService;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    private User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public PagedResponse<PersonResponse> getAllPeople(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Long userId = getCurrentUserId();
        
        Page<Person> peoplePage;
        if (search != null && !search.trim().isEmpty()) {
            peoplePage = personRepository.findByUserIdAndSearch(userId, search.trim(), pageable);
        } else {
            peoplePage = personRepository.findByUserId(userId, pageable);
        }

        List<PersonResponse> content = peoplePage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                peoplePage.getNumber(),
                peoplePage.getSize(),
                peoplePage.getTotalElements(),
                peoplePage.getTotalPages(),
                peoplePage.isLast()
        );
    }

    public PersonResponse getPersonById(Long id) {
        Person person = personRepository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        return mapToResponse(person);
    }

    public PersonResponse createPerson(PersonRequest request) {
        Person person = Person.builder()
                .name(request.getName())
                .email(request.getEmail())
                .dob(request.getDob())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .phone(request.getPhone())
                .relationship(request.getRelationship())
                .user(getCurrentUser())
                .build();
                
        Person savedPerson = personRepository.save(person);
        return mapToResponse(savedPerson);
    }

    public PersonResponse updatePerson(Long id, PersonRequest request) {
        Person person = personRepository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        person.setName(request.getName());
        person.setEmail(request.getEmail());
        person.setDob(request.getDob());
        person.setDepartment(request.getDepartment());
        person.setDesignation(request.getDesignation());
        person.setPhone(request.getPhone());
        person.setRelationship(request.getRelationship());

        Person updatedPerson = personRepository.save(person);
        return mapToResponse(updatedPerson);
    }

    public void deletePerson(Long id) {
        Person person = personRepository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        personRepository.delete(person);
    }

    public List<PersonResponse> bulkCreatePeople(MultipartFile file) {
        if (!excelService.hasExcelFormat(file)) {
            throw new com.birthdayposter.exception.BadRequestException("Please upload an Excel file (.xlsx)!");
        }

        try {
            List<PersonRequest> requests = excelService.parseExcelFile(file.getInputStream());
            User currentUser = getCurrentUser();

            List<Person> people = requests.stream()
                    .map(req -> Person.builder()
                            .name(req.getName())
                            .email(req.getEmail())
                            .dob(req.getDob())
                            .department(req.getDepartment())
                            .designation(req.getDesignation())
                            .phone(req.getPhone())
                            .relationship(req.getRelationship())
                            .user(currentUser)
                            .build())
                    .collect(Collectors.toList());

            List<Person> savedPeople = personRepository.saveAll(people);
            return savedPeople.stream().map(this::mapToResponse).collect(Collectors.toList());
        } catch (IOException e) {
            throw new com.birthdayposter.exception.BadRequestException("Failed to process Excel file: " + e.getMessage());
        }
    }

    private PersonResponse mapToResponse(Person person) {
        PersonResponse response = new PersonResponse();
        response.setId(person.getId());
        response.setName(person.getName());
        response.setEmail(person.getEmail());
        response.setDob(person.getDob());
        response.setPhone(person.getPhone());
        response.setDepartment(person.getDepartment());
        response.setDesignation(person.getDesignation());
        response.setRelationship(person.getRelationship());
        response.setPhotoUrl(person.getPhotoUrl());
        response.setCreatedAt(person.getCreatedAt());
        response.setUpdatedAt(person.getUpdatedAt());
        return response;
    }
}

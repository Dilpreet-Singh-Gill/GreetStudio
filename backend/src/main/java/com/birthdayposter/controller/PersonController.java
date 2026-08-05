package com.birthdayposter.controller;

import com.birthdayposter.dto.ApiResponse;
import com.birthdayposter.dto.PagedResponse;
import com.birthdayposter.dto.PersonRequest;
import com.birthdayposter.dto.PersonResponse;
import com.birthdayposter.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<PagedResponse<PersonResponse>> getAllPeople(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(personService.getAllPeople(page, size, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@Valid @RequestBody PersonRequest request) {
        return ResponseEntity.ok(personService.createPerson(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> updatePerson(@PathVariable Long id, @Valid @RequestBody PersonRequest request) {
        return ResponseEntity.ok(personService.updatePerson(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.ok(new ApiResponse(true, "Person deleted successfully"));
    }
}

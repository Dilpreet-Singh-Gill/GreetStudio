package com.birthdayposter.service;

import com.birthdayposter.dto.PersonRequest;
import com.birthdayposter.dto.PersonResponse;
import com.birthdayposter.entity.Person;
import com.birthdayposter.entity.User;
import com.birthdayposter.repository.PersonRepository;
import com.birthdayposter.repository.UserRepository;
import com.birthdayposter.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    private PersonRepository personRepository;
    private UserRepository userRepository;
    private ExcelService excelService;
    private PersonService personService;

    private User mockUser;
    private Person mockPerson;

    @BeforeEach
    void setUp() {
        // Mock interfaces using JDK dynamic proxies, use real instance for class to avoid Mockito Java 25 issues
        personRepository = mock(PersonRepository.class);
        userRepository = mock(UserRepository.class);
        excelService = new ExcelService();
        personService = new PersonService(personRepository, userRepository, excelService);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setRole(com.birthdayposter.entity.Role.ROLE_USER);

        mockPerson = new Person();
        mockPerson.setId(1L);
        mockPerson.setName("John Doe");
        mockPerson.setDob(LocalDate.of(1990, 1, 1));
        mockPerson.setUser(mockUser);

        // Mock Security Context
        UserDetailsImpl userDetails = UserDetailsImpl.build(mockUser);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createPerson_Success() {
        PersonRequest request = new PersonRequest();
        request.setName("John Doe");
        request.setDob(LocalDate.of(1990, 1, 1));
        request.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);

        PersonResponse response = personService.createPerson(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void getPersonById_Success() {
        when(personRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockPerson));

        PersonResponse response = personService.getPersonById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
    }

    @Test
    void deletePerson_Success() {
        when(personRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockPerson));
        
        personService.deletePerson(1L);

        verify(personRepository, times(1)).delete(mockPerson);
    }
}

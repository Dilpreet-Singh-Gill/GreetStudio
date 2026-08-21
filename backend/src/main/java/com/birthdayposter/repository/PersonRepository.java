package com.birthdayposter.repository;

import com.birthdayposter.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Page<Person> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Person p WHERE p.user.id = :userId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.department) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Person> findByUserIdAndSearch(@Param("userId") Long userId, @Param("search") String search, Pageable pageable);
    
    Optional<Person> findByIdAndUserId(Long id, Long userId);
    
    long countByUserId(Long userId);
    
    @Query("SELECT p FROM Person p WHERE p.user.id = :userId AND MONTH(p.dob) = :month AND DAY(p.dob) = :day")
    List<Person> findByUserIdAndBirthMonthAndDay(@Param("userId") Long userId, @Param("month") int month, @Param("day") int day);

    List<Person> findByUserId(Long userId);
}

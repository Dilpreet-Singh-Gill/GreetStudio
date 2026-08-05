package com.birthdayposter.repository;

import com.birthdayposter.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Page<Person> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Person p WHERE p.user.id = :userId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.department) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Person> findByUserIdAndSearch(@Param("userId") Long userId, @Param("search") String search, Pageable pageable);
    
    Optional<Person> findByIdAndUserId(Long id, Long userId);
}

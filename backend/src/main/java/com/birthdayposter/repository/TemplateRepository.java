package com.birthdayposter.repository;

import com.birthdayposter.entity.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    Page<Template> findByUserId(Long userId, Pageable pageable);

    Optional<Template> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}

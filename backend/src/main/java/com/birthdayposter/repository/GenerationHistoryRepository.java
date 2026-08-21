package com.birthdayposter.repository;

import com.birthdayposter.entity.GenerationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenerationHistoryRepository extends JpaRepository<GenerationHistory, Long> {
    Page<GenerationHistory> findByUserId(Long userId, Pageable pageable);
    
    long countByUserId(Long userId);
    
    java.util.List<GenerationHistory> findTop3ByUserIdOrderByCreatedAtDesc(Long userId);
}

package com.blog.platform.repository;

import com.blog.platform.entity.TechQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechQuestionRepository extends JpaRepository<TechQuestion, Long> {

    List<TechQuestion> findAllByOrderByCreatedAtDesc();

    List<TechQuestion> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    List<TechQuestion> findByStatusOrderByCreatedAtDesc(TechQuestion.Status status);
}

package com.blog.platform.repository;

import com.blog.platform.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    List<Solution> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    long countByQuestionId(Long questionId);
}

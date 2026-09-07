package com.blog.platform.repository;

import com.blog.platform.entity.AiOpinion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiOpinionRepository extends JpaRepository<AiOpinion, Long> {

    List<AiOpinion> findAllByOrderByCreatedAtDesc();

    List<AiOpinion> findByIsTopicTrueOrderByCreatedAtDesc();
}

package com.blog.platform.repository;

import com.blog.platform.entity.OpinionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpinionCommentRepository extends JpaRepository<OpinionComment, Long> {

    List<OpinionComment> findByOpinionIdOrderByCreatedAtAsc(Long opinionId);

    long countByOpinionId(Long opinionId);
}

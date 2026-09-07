package com.blog.platform.service;

import com.blog.platform.dto.AiOpinionRequest;
import com.blog.platform.entity.AiOpinion;
import com.blog.platform.entity.OpinionComment;
import com.blog.platform.entity.User;
import com.blog.platform.repository.AiOpinionRepository;
import com.blog.platform.repository.OpinionCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class AiOpinionService {

    private final AiOpinionRepository opinionRepository;
    private final OpinionCommentRepository commentRepository;
    private final UserService userService;
    private final Random random = new Random();

    private static final String[] STAR_COLORS = {
            "#FFD700", "#FF6B6B", "#4ECDC4", "#45B7D1",
            "#96CEB4", "#FFEAA7", "#DDA0DD", "#F7DC6F",
            "#85C1E9", "#F1948A", "#82E0AA", "#F8C471"
    };

    public AiOpinionService(AiOpinionRepository opinionRepository,
                            OpinionCommentRepository commentRepository,
                            UserService userService) {
        this.opinionRepository = opinionRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    /**
     * 发布观点（生成一颗星星）
     */
    public AiOpinion create(AiOpinionRequest request, Long authorId) {
        User author = userService.getById(authorId);
        AiOpinion opinion = new AiOpinion();
        opinion.setTitle(request.getTitle());
        opinion.setContent(request.getContent());
        opinion.setAuthor(author);
        opinion.setStarColor(request.getStarColor() != null
                ? request.getStarColor() : STAR_COLORS[random.nextInt(STAR_COLORS.length)]);
        opinion.setStarSize(request.getStarSize() != null ? request.getStarSize() : 2 + random.nextInt(3));
        // 随机星空位置
        opinion.setPosX(request.getPosX() != null ? request.getPosX() : 5 + random.nextDouble() * 90);
        opinion.setPosY(request.getPosY() != null ? request.getPosY() : 5 + random.nextDouble() * 85);
        return opinionRepository.save(opinion);
    }

    /**
     * 获取所有观点（星星）
     */
    public List<AiOpinion> findAll() {
        return opinionRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 获取观点详情
     */
    public AiOpinion getById(Long id) {
        return opinionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("观点不存在"));
    }

    /**
     * 点击观点 -> 转化为议题（讨论区）
     */
    @Transactional
    public AiOpinion convertToTopic(Long id) {
        AiOpinion opinion = getById(id);
        opinion.setIsTopic(true);
        return opinionRepository.save(opinion);
    }

    /**
     * 点赞观点
     */
    @Transactional
    public AiOpinion like(Long id) {
        AiOpinion opinion = getById(id);
        opinion.setLikeCount(opinion.getLikeCount() + 1);
        return opinionRepository.save(opinion);
    }

    /**
     * 发表议题讨论
     */
    @Transactional
    public OpinionComment addComment(Long opinionId, String content, Long authorId) {
        AiOpinion opinion = getById(opinionId);
        User author = userService.getById(authorId);
        OpinionComment comment = new OpinionComment();
        comment.setOpinion(opinion);
        comment.setAuthor(author);
        comment.setContent(content);
        OpinionComment saved = commentRepository.save(comment);
        // 更新讨论数
        opinion.setCommentCount((int) commentRepository.countByOpinionId(opinionId));
        opinionRepository.save(opinion);
        return saved;
    }

    /**
     * 获取议题讨论列表
     */
    public List<OpinionComment> getComments(Long opinionId) {
        return commentRepository.findByOpinionIdOrderByCreatedAtAsc(opinionId);
    }
}

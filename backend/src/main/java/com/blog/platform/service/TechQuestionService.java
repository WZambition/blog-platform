package com.blog.platform.service;

import com.blog.platform.dto.TechQuestionRequest;
import com.blog.platform.entity.Notification;
import com.blog.platform.entity.Solution;
import com.blog.platform.entity.TechQuestion;
import com.blog.platform.entity.User;
import com.blog.platform.repository.NotificationRepository;
import com.blog.platform.repository.SolutionRepository;
import com.blog.platform.repository.TechQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TechQuestionService {

    private final TechQuestionRepository questionRepository;
    private final SolutionRepository solutionRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public TechQuestionService(TechQuestionRepository questionRepository,
                               SolutionRepository solutionRepository,
                               NotificationRepository notificationRepository,
                               UserService userService) {
        this.questionRepository = questionRepository;
        this.solutionRepository = solutionRepository;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    /**
     * 发布技术问题
     */
    public TechQuestion create(TechQuestionRequest request, Long authorId) {
        User author = userService.getById(authorId);
        TechQuestion question = new TechQuestion();
        question.setTitle(request.getTitle());
        question.setDescription(request.getDescription());
        question.setImageUrl(request.getImageUrl());
        question.setTags(request.getTags());
        question.setAuthor(author);
        question.setStatus(TechQuestion.Status.UNSOLVED);
        return questionRepository.save(question);
    }

    /**
     * 获取所有技术问题
     */
    public List<TechQuestion> findAll() {
        return questionRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 获取问题详情（浏览数+1）
     */
    @Transactional
    public TechQuestion getById(Long id) {
        TechQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("问题不存在"));
        question.setViewCount(question.getViewCount() + 1);
        return questionRepository.save(question);
    }

    /**
     * 获取问题详情（不增加浏览数，内部使用）
     */
    public TechQuestion getByIdInternal(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("问题不存在"));
    }

    /**
     * 获取问题的所有方案
     */
    public List<Solution> getSolutions(Long questionId) {
        return solutionRepository.findByQuestionIdOrderByCreatedAtAsc(questionId);
    }

    /**
     * 提交方案（别人回答）
     * 提交后：问题状态变为 VERIFYING(验证中)，并给问题发起者发送消息提醒
     */
    @Transactional
    public Solution submitSolution(Long questionId, String content, Long authorId) {
        TechQuestion question = getByIdInternal(questionId);
        User author = userService.getById(authorId);

        // 问题发起者不能给自己的问题提交方案（可选限制，这里允许但提示）
        Solution solution = new Solution();
        solution.setQuestion(question);
        solution.setAuthor(author);
        solution.setContent(content);
        solution.setAccepted(false);
        Solution saved = solutionRepository.save(solution);

        // 更新方案数量
        question.setSolutionCount((int) solutionRepository.countByQuestionId(questionId));

        // 问题状态变为验证中（除非已经是已解决）
        if (question.getStatus() != TechQuestion.Status.SOLVED) {
            question.setStatus(TechQuestion.Status.VERIFYING);
        }
        questionRepository.save(question);

        // 给问题发起者发送消息提醒（如果提交者不是发起者本人）
        if (!question.getAuthor().getId().equals(authorId)) {
            Notification notification = new Notification();
            notification.setReceiver(question.getAuthor());
            notification.setActor(author);
            notification.setType(Notification.Type.NEW_SOLUTION);
            notification.setQuestion(question);
            notification.setContent("用户「" + author.getNickname() + "」为你的问题《" + question.getTitle()
                    + "》提交了一个方案，请前往验证。");
            notificationRepository.save(notification);
        }

        return saved;
    }

    /**
     * 修改方案（问题发起者或方案作者可修改）
     */
    @Transactional
    public Solution updateSolution(Long solutionId, String content, Long userId) {
        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new IllegalArgumentException("方案不存在"));
        // 仅方案作者可修改
        if (!solution.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("只有方案作者可以修改方案");
        }
        solution.setContent(content);
        return solutionRepository.save(solution);
    }

    /**
     * 采纳方案（问题发起者操作）
     * 采纳后：问题状态变为 SOLVED(已解决)，方案标记为已采纳
     */
    @Transactional
    public Solution acceptSolution(Long solutionId, Long userId) {
        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new IllegalArgumentException("方案不存在"));
        TechQuestion question = solution.getQuestion();

        // 仅问题发起者可采纳
        if (!question.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("只有问题发起者可以采纳方案");
        }

        // 取消其他方案的采纳状态
        List<Solution> allSolutions = solutionRepository.findByQuestionIdOrderByCreatedAtAsc(question.getId());
        for (Solution s : allSolutions) {
            if (s.getAccepted()) {
                s.setAccepted(false);
                solutionRepository.save(s);
            }
        }

        solution.setAccepted(true);
        Solution saved = solutionRepository.save(solution);

        // 问题状态变为已解决
        question.setStatus(TechQuestion.Status.SOLVED);
        questionRepository.save(question);

        // 给方案作者发送消息提醒
        if (!solution.getAuthor().getId().equals(userId)) {
            Notification notification = new Notification();
            notification.setReceiver(solution.getAuthor());
            notification.setActor(question.getAuthor());
            notification.setType(Notification.Type.SOLUTION_ACCEPTED);
            notification.setQuestion(question);
            notification.setContent("你的方案已被问题《" + question.getTitle() + "》的发起者采纳！");
            notificationRepository.save(notification);
        }

        return saved;
    }

    /**
     * 问题发起者将问题重新标记为未解决（验证不通过时）
     */
    @Transactional
    public TechQuestion markUnsolved(Long questionId, Long userId) {
        TechQuestion question = getByIdInternal(questionId);
        if (!question.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("只有问题发起者可以修改状态");
        }
        question.setStatus(TechQuestion.Status.UNSOLVED);
        return questionRepository.save(question);
    }

    /**
     * 问题发起者将问题标记为已解决（无需采纳方案）
     */
    @Transactional
    public TechQuestion markSolved(Long questionId, Long userId) {
        TechQuestion question = getByIdInternal(questionId);
        if (!question.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("只有问题发起者可以修改状态");
        }
        question.setStatus(TechQuestion.Status.SOLVED);
        return questionRepository.save(question);
    }
}

package com.blog.platform.controller;

import com.blog.platform.common.ApiResponse;
import com.blog.platform.dto.SolutionRequest;
import com.blog.platform.dto.TechQuestionRequest;
import com.blog.platform.entity.Solution;
import com.blog.platform.entity.TechQuestion;
import com.blog.platform.service.TechQuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class TechQuestionController {

    private final TechQuestionService questionService;

    public TechQuestionController(TechQuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * 发布技术问题
     */
    @PostMapping
    public ApiResponse<TechQuestion> create(@RequestBody TechQuestionRequest request,
                                            @RequestParam Long authorId) {
        TechQuestion question = questionService.create(request, authorId);
        return ApiResponse.success("问题发布成功", question);
    }

    /**
     * 获取所有技术问题
     */
    @GetMapping
    public ApiResponse<List<TechQuestion>> findAll() {
        return ApiResponse.success(questionService.findAll());
    }

    /**
     * 获取问题详情（浏览数+1）
     */
    @GetMapping("/{id}")
    public ApiResponse<TechQuestion> getById(@PathVariable Long id) {
        return ApiResponse.success(questionService.getById(id));
    }

    /**
     * 获取问题的所有方案
     */
    @GetMapping("/{id}/solutions")
    public ApiResponse<List<Solution>> getSolutions(@PathVariable Long id) {
        return ApiResponse.success(questionService.getSolutions(id));
    }

    /**
     * 提交方案（别人回答）
     * 提交后问题状态变为验证中，并给问题发起者发送消息提醒
     */
    @PostMapping("/{id}/solutions")
    public ApiResponse<Solution> submitSolution(@PathVariable Long id,
                                                @RequestBody SolutionRequest request,
                                                @RequestParam Long authorId) {
        Solution solution = questionService.submitSolution(id, request.getContent(), authorId);
        return ApiResponse.success("方案提交成功，问题已标记为验证中", solution);
    }

    /**
     * 修改方案（方案作者）
     */
    @PutMapping("/solutions/{solutionId}")
    public ApiResponse<Solution> updateSolution(@PathVariable Long solutionId,
                                                @RequestBody SolutionRequest request,
                                                @RequestParam Long userId) {
        Solution solution = questionService.updateSolution(solutionId, request.getContent(), userId);
        return ApiResponse.success("方案修改成功", solution);
    }

    /**
     * 采纳方案（问题发起者）
     * 采纳后问题状态变为已解决
     */
    @PostMapping("/solutions/{solutionId}/accept")
    public ApiResponse<Solution> acceptSolution(@PathVariable Long solutionId,
                                                @RequestParam Long userId) {
        Solution solution = questionService.acceptSolution(solutionId, userId);
        return ApiResponse.success("方案已采纳，问题已解决", solution);
    }

    /**
     * 问题发起者将问题标记为未解决（验证不通过）
     */
    @PostMapping("/{id}/unsolved")
    public ApiResponse<TechQuestion> markUnsolved(@PathVariable Long id,
                                                  @RequestParam Long userId) {
        TechQuestion question = questionService.markUnsolved(id, userId);
        return ApiResponse.success("问题已标记为未解决", question);
    }

    /**
     * 问题发起者将问题标记为已解决
     */
    @PostMapping("/{id}/solved")
    public ApiResponse<TechQuestion> markSolved(@PathVariable Long id,
                                                @RequestParam Long userId) {
        TechQuestion question = questionService.markSolved(id, userId);
        return ApiResponse.success("问题已标记为已解决", question);
    }
}

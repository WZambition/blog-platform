package com.blog.platform.controller;

import com.blog.platform.common.ApiResponse;
import com.blog.platform.dto.AiOpinionRequest;
import com.blog.platform.dto.OpinionCommentRequest;
import com.blog.platform.entity.AiOpinion;
import com.blog.platform.entity.OpinionComment;
import com.blog.platform.service.AiOpinionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opinions")
public class AiOpinionController {

    private final AiOpinionService opinionService;

    public AiOpinionController(AiOpinionService opinionService) {
        this.opinionService = opinionService;
    }

    /**
     * 发布观点（生成星星）
     */
    @PostMapping
    public ApiResponse<AiOpinion> create(@RequestBody AiOpinionRequest request,
                                         @RequestParam Long authorId) {
        AiOpinion opinion = opinionService.create(request, authorId);
        return ApiResponse.success("观点发布成功", opinion);
    }

    /**
     * 获取所有观点（星星）
     */
    @GetMapping
    public ApiResponse<List<AiOpinion>> findAll() {
        return ApiResponse.success(opinionService.findAll());
    }

    /**
     * 获取观点详情
     */
    @GetMapping("/{id}")
    public ApiResponse<AiOpinion> getById(@PathVariable Long id) {
        return ApiResponse.success(opinionService.getById(id));
    }

    /**
     * 点击观点 -> 转化为议题
     */
    @PostMapping("/{id}/topic")
    public ApiResponse<AiOpinion> convertToTopic(@PathVariable Long id) {
        AiOpinion opinion = opinionService.convertToTopic(id);
        return ApiResponse.success("观点已转化为议题", opinion);
    }

    /**
     * 点赞观点
     */
    @PostMapping("/{id}/like")
    public ApiResponse<AiOpinion> like(@PathVariable Long id) {
        return ApiResponse.success(opinionService.like(id));
    }

    /**
     * 获取议题讨论列表
     */
    @GetMapping("/{id}/comments")
    public ApiResponse<List<OpinionComment>> getComments(@PathVariable Long id) {
        return ApiResponse.success(opinionService.getComments(id));
    }

    /**
     * 发表议题讨论
     */
    @PostMapping("/{id}/comments")
    public ApiResponse<OpinionComment> addComment(@PathVariable Long id,
                                                  @RequestBody OpinionCommentRequest request,
                                                  @RequestParam Long authorId) {
        OpinionComment comment = opinionService.addComment(id, request.getContent(), authorId);
        return ApiResponse.success("讨论发表成功", comment);
    }
}

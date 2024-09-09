package com.munecting.api.domain.comment.controller;

import com.munecting.api.domain.comment.dto.request.CommentRequestDto;
import com.munecting.api.domain.comment.dto.response.CommentIdResponseDto;
import com.munecting.api.domain.comment.dto.response.CommentResponseDto;
import com.munecting.api.domain.comment.service.CommentService;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.PagedResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/tracks")
@Tag(name = "comments", description = "댓글 관련 api </br> <i> 담당자 : 전민주 </i>")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    @Operation(summary = "댓글 등록하기")
    public ApiResponse<?> createComment(
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        CommentIdResponseDto commentIdResponseDto = commentService.createComment(commentRequestDto);
        return ApiResponse.created(commentIdResponseDto);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제하기")
    public ApiResponse<?> deleteComment(
            @PathVariable Long commentId
    ) {
        CommentIdResponseDto commentIdResponseDto = commentService.deleteCommentById(commentId);
        return ApiResponse.ok(commentIdResponseDto);
    }

    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정하기")
    public ApiResponse<?> updateComment(
            @PathVariable (name = "commentId") Long commentId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        CommentIdResponseDto commentIdResponseDto = commentService.updateComment(commentId, commentRequestDto);
        return ApiResponse.ok(commentIdResponseDto);
    }

    @GetMapping("/{trackId}/comments")
    @Operation(summary = "댓글 조회하기")
    public ApiResponse<?> getCommentsByTrackId(
            @PathVariable (name = "trackId") String trackId,
            @RequestParam (name = "cursor") LocalDateTime cursor,
            @RequestParam (name = "limit") int limit
    ) {
        PagedResponseDto<CommentResponseDto> commentResponseDtoList = commentService.getCommentsByTrackId(trackId, cursor, limit);
        return ApiResponse.ok(commentResponseDtoList);
    }
}

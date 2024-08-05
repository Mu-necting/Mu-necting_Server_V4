package com.munecting.api.domain.comment.controller;

import com.munecting.api.domain.comment.dto.CommentRequestDto;
import com.munecting.api.domain.comment.dto.CommentResponseDto;
import com.munecting.api.domain.comment.entity.Comment;
import com.munecting.api.domain.comment.service.CommentService;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.PagedResponseDto;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/comment")
@Tag(name = "comment", description = "댓글 관련 api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/")
    @Operation(summary = "댓글 등록하기")
    public ApiResponse<?> createComment(
            @RequestBody CommentRequestDto commentRequestDto) {
        Long id = commentService.createComment(commentRequestDto);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), id);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제하기")
    public ApiResponse<?> deleteComment(
            @PathVariable Long commentId) {
        Long id = commentService.deleteComment(commentId);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), id);
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정하기")
    public ApiResponse<?> deleteComment(
            @PathVariable (name = "commentId") Long commentId,
            @RequestBody CommentRequestDto commentRequestDto) {
        Long id = commentService.updateComment(commentId, commentRequestDto);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), id);
    }

    @GetMapping("/{trackId}")
    @Operation(summary = "댓글 조회하기")
    public ApiResponse<?> getCommentsByTrackId(
            @PathVariable (name = "trackId") String trackId,
            @RequestParam (name = "cursor") LocalDateTime cursor,
            @RequestParam (name = "limit") int limit) {
        PagedResponseDto<CommentResponseDto> commentResponseDtoList = commentService.getCommentsByTrackId(trackId, cursor, limit);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), commentResponseDtoList);
    }

}

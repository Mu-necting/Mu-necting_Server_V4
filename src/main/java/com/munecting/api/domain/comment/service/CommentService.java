package com.munecting.api.domain.comment.service;

import com.munecting.api.domain.comment.dao.CommentRepository;
import com.munecting.api.domain.comment.dto.request.CommentRequestDto;
import com.munecting.api.domain.comment.dto.response.CommentIdResponseDto;
import com.munecting.api.domain.comment.dto.response.CommentResponseDto;
import com.munecting.api.domain.comment.entity.Comment;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.user.dto.response.UserResponseDto;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.domain.user.service.UserService;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.common.dto.response.PagedResponseDto;
import com.munecting.api.global.common.dto.response.Status;

import com.munecting.api.global.error.exception.UnauthorizedException;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final SpotifyService spotifyService;
    private final UserService userService;

    @Transactional
    public CommentIdResponseDto createComment(Long userId, CommentRequestDto commentRequestDto) {
        userService.validateUserExists(userId);
        String trackId = commentRequestDto.trackId();
        spotifyService.validateTrackExists(trackId);
        Comment comment = Comment.toEntity(userId, commentRequestDto);
        Long id = saveComment(comment);
        return CommentIdResponseDto.of(id);
    }

    private Long saveComment(Comment comment) {
        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<CommentResponseDto> getCommentsByTrackId(Long userId, String trackId, LocalDateTime cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Comment> pagedComment = getCommentsByTrackIdWithCursor(trackId, cursor, pageable);
        List<CommentResponseDto> commentResponseDtos = pagedComment.stream()
                .map(comment -> {
                    User commentWriter = userService.findUserByIdOrThrow(comment.getUserId());
                    UserResponseDto userResponseDto = UserResponseDto.of(commentWriter.getId(), commentWriter.getNickname(), commentWriter.getProfileImageUrl());
                    Boolean isOwner = userId.equals(commentWriter.getId());
                    return CommentResponseDto.of(userResponseDto, comment, isOwner);
                })
                .collect(Collectors.toList());

        Page<CommentResponseDto> pagedCommentResponseDto = new PageImpl<>(commentResponseDtos, pageable, pagedComment.getTotalElements());
        return new PagedResponseDto<>(pagedCommentResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByTrackIdWithCursor(String trackId, LocalDateTime cursor, Pageable pageable) {
        log.info(String.valueOf(cursor));
        return commentRepository.findCommentsByTrackIdWithCursor(trackId, cursor.toString(), pageable);
    }

    @Transactional
    public CommentIdResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = getCommentById(commentId);
        validateCommentWriter(userId, comment);
        comment.updateContent(commentRequestDto.content());
        return CommentIdResponseDto.of(commentId);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Status.COMMENT_NOT_FOUND));
    }

    @Transactional
    public CommentIdResponseDto deleteCommentById(Long userId, Long commentId) {
        Comment comment = getCommentById(commentId);
        validateCommentWriter(userId, comment);
        deleteComment(comment);
        return CommentIdResponseDto.of(commentId);
    }

    private void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    private void validateCommentWriter(Long userId, Comment comment) {
        if (!userId.equals(comment.getUserId())) {
            throw new UnauthorizedException(Status.NOT_COMMENT_WRITER);
        }
    }
}

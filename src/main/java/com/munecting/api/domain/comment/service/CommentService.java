package com.munecting.api.domain.comment.service;

import com.munecting.api.domain.comment.dao.CommentRepository;
import com.munecting.api.domain.comment.dto.CommentRequestDto;
import com.munecting.api.domain.comment.dto.CommentResponseDto;
import com.munecting.api.domain.comment.entity.Comment;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.common.dto.response.PagedResponseDto;
import com.munecting.api.global.common.dto.response.Status;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final SpotifyService spotifyService;

    public Long createComment(CommentRequestDto commentRequestDto) {
        String trackId = commentRequestDto.getTrackId();
        spotifyService.getTrack(trackId);
        Comment comment = Comment.toEntity(commentRequestDto);
        Long id = saveCommentEntity(comment);
        return id;
    }

    public Long deleteComment(Long commentId) {
        Comment comment = getCommentEntityById(commentId);
        deleteCommentEntity(comment);
        return commentId;
    }

    public Long updateComment(Long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = getCommentEntityById(commentId);
        comment.updateContent(commentRequestDto.getContent());
        return saveCommentEntity(comment);
    }

    public PagedResponseDto<CommentResponseDto> getCommentsByTrackId(String trackId, LocalDateTime cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        //Timestamp cursorTimestamp = LocalDateTimeUtil.toTimestamp(cursor);
        Page<Comment> pagedComment = getCommentsByTrackIdWithCursor(trackId, cursor, pageable);
        Page<CommentResponseDto> pagedCommentResponseDto = pagedComment.map(CommentResponseDto::toDto);
        return new PagedResponseDto<>(pagedCommentResponseDto);
    }

    public Long saveCommentEntity(Comment comment) {
        return commentRepository.save(comment).getId();
    }

    public void deleteCommentEntity(Comment comment) {
        commentRepository.delete(comment);
    }

    public Page<Comment> getCommentsByTrackIdWithCursor(String trackId, LocalDateTime cursor, Pageable pageable) {
        log.info(String.valueOf(cursor));
        return commentRepository.findCommentsByTrackIdWithCursor(trackId, cursor.toString(), pageable);
    }

    public Comment getCommentEntityById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Status.COMMENT_NOT_FOUND));
    }

}
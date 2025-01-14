package com.sparta.vicky.comment.service;

import com.sparta.vicky.board.entity.Board;
import com.sparta.vicky.board.service.BoardService;
import com.sparta.vicky.comment.Repository.CommentRepository;
import com.sparta.vicky.comment.dto.CommentRequest;
import com.sparta.vicky.comment.entity.Comment;
import com.sparta.vicky.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardService boardService;

    /**
     * 댓글 작성
     */
    @Transactional
    public Comment createComment(CommentRequest request, User user) {
        Board board = boardService.getBoard(request.getBoardId());
        Comment comment = new Comment(request, board, user);

        return commentRepository.save(comment);
    }

    /**
     * 특정 게시물의 전체 댓글 조회
     */
    public List<Comment> getAllComments(Long boardId) {
        return commentRepository.findAllByBoardIdOrderByCreatedAt(boardId);
    }

    /**
     * 특정 댓글 조회
     */
    public Comment getComment(Long boardId, Long commentId) {
        Comment comment = findById(commentId);
        comment.verifyBoard(boardId);

        return comment;
    }

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("commentId " + commentId + " 에 해당하는 댓글이 존재하지 않습니다."));
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public Comment updateComment(Long boardId, Long commentId, CommentRequest request, Long userId) {
        Comment comment = getComment(boardId, commentId);
        comment.verifyUser(userId);
        comment.update(request);

        return comment;
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public Long deleteComment(Long boardId, Long commentId, Long userId) {
        Comment comment = getComment(boardId, commentId);
        comment.verifyUser(userId);
        commentRepository.delete(comment);

        return commentId;
    }

}

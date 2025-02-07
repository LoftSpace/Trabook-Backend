package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.dto.CommentRequestDTO;
import Trabook.PlanManager.response.CommentUpdateResponseDTO;
import Trabook.PlanManager.response.ResponseMessage;
import Trabook.PlanManager.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plan/comment")
@RequiredArgsConstructor
public class PlanCommentController {
    private final PlanService planService;

    @ResponseBody
    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentRequestDTO comment, @RequestHeader("userId") Long userId) {
        if(userId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userId 없음");

        try {
            comment.setUserId(userId);
            long providedCommentId = planService.addComment(comment);
            return ResponseEntity.ok(
                    CommentUpdateResponseDTO.builder()
                            .commentId(providedCommentId)
                            .message("댓글 작성 성공")
                            .build());

        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @ResponseBody
    @DeleteMapping("")
    public ResponseEntity<?> deleteComment(@RequestParam("commentId") Long commentId, @RequestHeader("userId") Long userId) {
        try {
            planService.deleteComment(commentId);
            return ResponseEntity.ok("댓글 삭제 성공");
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

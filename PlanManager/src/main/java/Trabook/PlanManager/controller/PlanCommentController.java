package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.comment.CommentRequestDTO;
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
    public ResponseEntity<CommentUpdateResponseDTO> addComment(@RequestBody CommentRequestDTO comment, @RequestHeader("userId") long userId) {
        comment.setUserId(userId);
        comment.setCommentId(0);
        CommentUpdateResponseDTO commentUpdateResponseDTO = new CommentUpdateResponseDTO();
        try {
            commentUpdateResponseDTO.setCommentId(planService.addComment(comment));
            commentUpdateResponseDTO.setMessage("comment added");
            return ResponseEntity.ok(commentUpdateResponseDTO);
        } catch (IllegalArgumentException e){
            commentUpdateResponseDTO.setMessage("no plan exists");
            return new ResponseEntity<>(commentUpdateResponseDTO, HttpStatus.NOT_FOUND);
        }

    }

    @ResponseBody
    @DeleteMapping("")
    public ResponseEntity<ResponseMessage> deleteComment(@RequestParam("commentId") long commentId, @RequestHeader("userId") long userId) {
        Comment comment = new Comment();
        try{
            comment = planService.getComment(commentId);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("comment not found"),HttpStatus.NOT_FOUND);
        }

        if(comment.getUser().getUserId() != userId)
            return new ResponseEntity<>(new ResponseMessage("you have no access to this comment"),HttpStatus.BAD_REQUEST);
        //유저아이디랑 댓글 아이디 일치여부 로직 추가
        String message = planService.deleteComment(commentId);
        return ResponseEntity.ok(new ResponseMessage(message));
    }
}

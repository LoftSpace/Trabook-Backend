package Trabook.PlanManager.dto;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class CommentWithUser {
    private Comment comment;
    private User user;

}

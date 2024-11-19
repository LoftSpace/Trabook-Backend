package Trabook.PlanManager.domain.comment;

import Trabook.PlanManager.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private User user;
    private long commentId;
    private long planId;
    private String content;
    private long parentId;
    private int refOrder;
    private String time;

}

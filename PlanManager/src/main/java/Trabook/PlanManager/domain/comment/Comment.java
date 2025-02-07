package Trabook.PlanManager.domain.comment;

import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.dto.CommentRequestDto;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long userId;
    private long commentId;
    private long planId;
    private String content;
    private long parentId;
    private int refOrder;
    private String time;

    public boolean isRootComment(){
        return this.refOrder == 0;

    }


}

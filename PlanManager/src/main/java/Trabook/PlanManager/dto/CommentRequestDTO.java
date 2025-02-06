package Trabook.PlanManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    private long commentId;
    private long userId;
    private long planId;
    private long parentId;
    private int refOrder;
    private String time;
    private String content;

}

package Trabook.PlanManager.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequestDTO {
    private long commentId;
    private Long userId;
    private Long planId;
    private long parentId;
    private int refOrder;
    private String time;
    private String content;

}

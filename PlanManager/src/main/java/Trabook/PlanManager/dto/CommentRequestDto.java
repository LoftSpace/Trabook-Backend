package Trabook.PlanManager.dto;

import Trabook.PlanManager.domain.comment.Comment;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequestDto {
    private long commentId;
    private Long userId;
    private Long planId;
    private long parentId;
    private int refOrder;
    private String time;
    private String content;

    public Comment toEntity() {
        return Comment.builder()
                .userId(userId)
                .commentId(commentId)
                .planId(planId)
                .parentId(parentId)
                .content(content)
                .refOrder(refOrder)
                .time(time)
                .build();
    }
}

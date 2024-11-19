package Trabook.PlanManager.domain.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    private long commentId;
    private long userId;
    private long planId;
    private String content;
    private long parentId;
    private int refOrder;
    private String time;

}

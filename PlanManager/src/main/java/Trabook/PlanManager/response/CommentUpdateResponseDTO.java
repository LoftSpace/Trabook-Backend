package Trabook.PlanManager.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CommentUpdateResponseDTO {
    private String message;
    private long commentId;

    public CommentUpdateResponseDTO() {}

    @Builder
    public CommentUpdateResponseDTO(String message, long commentId) {
        this.message = message;
        this.commentId = commentId;
    }
}

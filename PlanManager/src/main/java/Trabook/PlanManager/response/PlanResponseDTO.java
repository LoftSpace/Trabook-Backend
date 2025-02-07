package Trabook.PlanManager.response;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.dto.CommentWithUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseDTO {
    private Plan plan;
    private User planOwner;
    private List<CommentWithUser> comments;
    private Boolean isLiked;
    private Boolean isScrapped;
    private List<String> tags;

    public void isLiked(boolean planLiked) {
        this.isLiked = planLiked;
    }
    public void isScrapped(boolean planScrapped) {
        this.isScrapped = planScrapped;
    }
}

package Trabook.PlanManager.response;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.DayPlan;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseDTO {
    private Plan plan;
    private User user;
    private boolean isLiked;
    private boolean isScrapped;
    private List<String> tags;
    private List<Comment> comments;

}

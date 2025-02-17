package Trabook.PlanManager.dto;

import Trabook.PlanManager.domain.plan.PlanComment;
import Trabook.PlanManager.response.PlanListResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PlanGeneralDto {
    private PlanListResponseDTO plan;
    private List<PlanComment> comments;
    public PlanGeneralDto() {}

    public PlanGeneralDto(PlanListResponseDTO plan, List<PlanComment> comments) {
        this.plan = plan;
        this.comments = comments;
    }
}

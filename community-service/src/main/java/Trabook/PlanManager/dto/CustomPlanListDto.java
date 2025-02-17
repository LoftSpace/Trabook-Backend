package Trabook.PlanManager.dto;

import java.util.List;

public class CustomPlanListDto {
    public List<PlanGeneralDto> plans;
    public Integer totalPages;

    public CustomPlanListDto(List<PlanGeneralDto> plans, Integer totalPages) {
        this.plans = plans;
        this.totalPages = totalPages;
    }
}
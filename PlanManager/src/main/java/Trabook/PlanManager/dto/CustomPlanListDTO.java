package Trabook.PlanManager.dto;

import java.util.List;

public class CustomPlanListDTO {
    public List<PlanGeneralDTO> plans;
    public Integer totalPages;

    public CustomPlanListDTO(List<PlanGeneralDTO> plans, Integer totalPages) {
        this.plans = plans;
        this.totalPages = totalPages;
    }
}
package Trabook.PlanManager.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter @Setter
public class PlanCreateResponseDto {
    private long planId;
    private String message;
    private String imgSrc;

    public PlanCreateResponseDto(Long planId, String meesage, String fileName) {
        this.planId = planId;
        this.message = meesage;
        this.imgSrc = fileName;
    }
}

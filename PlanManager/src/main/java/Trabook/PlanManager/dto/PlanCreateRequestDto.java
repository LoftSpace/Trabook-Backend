package Trabook.PlanManager.dto;

import Trabook.PlanManager.domain.plan.PlanBasicInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanCreateRequestDto {
    private String state;
    private LocalDate startDate;
    private LocalDate endDate;


    public PlanBasicInfo toEntity(Long userId){
        return PlanBasicInfo.builder()
                .userId(userId)
                .state(state)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

}

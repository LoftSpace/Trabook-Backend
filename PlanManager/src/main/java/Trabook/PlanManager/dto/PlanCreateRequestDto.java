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
    private Long userId;
    private String state;
    private LocalDate startDate;
    private LocalDate endDate;

    public PlanCreateRequestDto(long userId, String state, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public PlanBasicInfo toEntity(){
        return PlanBasicInfo.builder()
                .userId(userId)
                .state(state)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

}

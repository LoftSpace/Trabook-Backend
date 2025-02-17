package Trabook.PlanManager.domain.plan;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanBasicInfo {
    private Long userId;
    private String state;
    private LocalDate startDate;
    private LocalDate endDate;
}

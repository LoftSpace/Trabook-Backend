package Trabook.PlanManager.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter @Getter
public class PlanCreateDto {
    private long userId;
    private String state;
    private LocalDate startDate;
    private LocalDate endDate;



    public PlanCreateDto(long userId, String state, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}

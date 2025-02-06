package Trabook.PlanManager.dto;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import lombok.*;

import java.util.List;
@Builder
@Getter @Setter
@AllArgsConstructor
public class PlaceForModalDTO {
    private Place place;
    private List<PlaceComment> comments;
    private Boolean isScrapped;


}

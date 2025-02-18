package Trabook.PlanManager.dto.DestinationDto;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
@SuperBuilder
@Getter @Setter
@AllArgsConstructor
public class PlaceForModalDTO {
    private Place place;
    private List<PlaceComment> comments;
    private Boolean isScrapped;

    public PlaceForModalDTO(Place place,List<PlaceComment> comments){
        this.place = place;
        this.comments = comments;
    }

}

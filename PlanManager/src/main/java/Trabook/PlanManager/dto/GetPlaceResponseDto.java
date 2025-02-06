package Trabook.PlanManager.dto;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

//@RequiredArgsConstructor
@Getter @Setter
public class GetPlaceResponseDto extends PlaceForModalDTO {

    private List<String> photos;

    public GetPlaceResponseDto(Place place, List<PlaceComment> comments, List<String> photos) {
        super(place, comments);
        this.photos = photos;
    }
}

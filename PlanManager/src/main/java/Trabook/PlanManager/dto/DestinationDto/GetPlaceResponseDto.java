package Trabook.PlanManager.dto.DestinationDto;

import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceComment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class GetPlaceResponseDto extends PlaceForModalDTO {

    private List<String> photos;

    @Builder
    public GetPlaceResponseDto(Place place, List<PlaceComment> comments, List<String> photos) {
        super(place, comments);
        this.photos = photos;
    }

}

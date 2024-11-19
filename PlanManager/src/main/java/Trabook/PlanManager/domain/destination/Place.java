package Trabook.PlanManager.domain.destination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    private Long placeId;
    private Long cityId;
    private String address;
    private String placeName;
    private String description;
    private Double latitude;
    private Double longitude;
    private Long star;
    private String category;
    private String imgSrc;
    private String subcategory;
    private Double ratingScore;
    private Integer scraps;
    private Integer numOfAdded;
    private Integer numOfReview;
    private Boolean isScrapped;

}

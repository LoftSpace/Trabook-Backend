package Trabook.PlanManager.domain.destination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    private long placeId;
    private long cityId;
    private String address;
    private String placeName;
    private String description;
    private double latitude;
    private double longitude;
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

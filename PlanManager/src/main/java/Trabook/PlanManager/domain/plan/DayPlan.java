package Trabook.PlanManager.domain.plan;

import Trabook.PlanManager.domain.destination.Place;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalTime;
import java.util.List;


@Getter @Setter
public class DayPlan {
    private long planId;
    private int day;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Schedule> scheduleList;

    @Getter
    @Setter
    public static class Schedule{
        private long planId;
        private int day;
        private int order;
        private int time;
        //about place
        private long placeId;
        private String imageSrc;
        private String placeName;
        private double latitude;
        private double longitude;
        private String address;
        private String subcategory;
        private int stars;
        private int numOfAdded;
        private int numOfReview;



        public Schedule() {}


        public void addDetails(Place place) {
            this.placeId = place.getPlaceId();
            this.imageSrc = place.getImgSrc();
            this.latitude = place.getLatitude();
            this.longitude = place.getLongitude();
            this.placeName = place.getPlaceName();
            this.subcategory = place.getSubcategory();
            this.address = place.getAddress();
        }
    }
    public DayPlan() {}

    public DayPlan( long planId, int day, LocalTime startTime, LocalTime endTime, List<Schedule> scheduleList) {
        this.planId = planId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleList = scheduleList;
    }
}

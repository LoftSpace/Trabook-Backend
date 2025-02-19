package Trabook.PlanManager.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class PlanListResponseDTO {

    public PlanListResponseDTO() {
    }

    private long planId;
    private String planTitle;
    private String description;
    private String state;
    //private 이미지
    private int likes;
    private int scraps;
    private int numOfComment;
    private String imgSrc;

    //여행 날짜
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isFinished;
    private Boolean isLiked;
    private Boolean isScrapped;
    private boolean isPublic;

    private Integer numOfPeople;

    private List<String> tags;

    public PlanListResponseDTO(long planId, String planTitle, String description, String state, int likes, int scraps, LocalDate startDate, LocalDate endDate, boolean isFinished, boolean isLiked, boolean isScrapped, boolean isPublic, List<String> tags) {
        this.planId = planId;
        this.planTitle = planTitle;
        this.description = description;
        this.state = state;
        this.likes = likes;
        this.scraps = scraps;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isFinished = isFinished;
        this.isLiked = isLiked;
        this.isScrapped = isScrapped;
        this.isPublic = isPublic;
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanListResponseDTO that = (PlanListResponseDTO) o;
        return planId == that.planId && likes == that.likes && scraps == that.scraps && numOfComment == that.numOfComment && isFinished == that.isFinished && isPublic == that.isPublic && Objects.equals(planTitle, that.planTitle) && Objects.equals(description, that.description) && Objects.equals(state, that.state) && Objects.equals(imgSrc, that.imgSrc) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(isLiked, that.isLiked) && Objects.equals(isScrapped, that.isScrapped) && Objects.equals(numOfPeople, that.numOfPeople) && Objects.equals(tags, that.tags);
    }


}

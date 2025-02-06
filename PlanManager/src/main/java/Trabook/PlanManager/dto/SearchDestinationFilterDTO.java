package Trabook.PlanManager.dto;

public class SearchDestinationFilterDTO {
    public SearchDestinationFilterDTO() {}
    private String keyword;
    private DestinationFilters destinationFilters;


    public static class DestinationFilters {
        private String City;
        private String category;
    }
}

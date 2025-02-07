package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.dto.PlanGeneralDto;
import Trabook.PlanManager.response.PlanListResponseDTO;

import java.util.List;

public interface PlanListRepository {

    List<PlanListResponseDTO> findUserPlanList(long userId);
    List<PlanListResponseDTO> findUserScrappedPlanList(long userId);
    List<PlanGeneralDto> findCustomPlanList(String search,
                                            List<String> state,
                                            Integer numOfPeople,
                                            Integer duration,
                                            String sorts,
                                            Integer userId,
                                            Boolean userScrapOnly);
    List<PlanListResponseDTO> findHottestPlan();


}

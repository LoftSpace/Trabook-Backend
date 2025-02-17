package Trabook.PlanManager.controller;
import Trabook.PlanManager.dto.CustomPlanListDto;
import Trabook.PlanManager.dto.PlanGeneralDto;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.service.PlanRedisService;
import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.planList.GetUserLikePlanList;
import Trabook.PlanManager.service.planList.GetUserPlanList;
import Trabook.PlanManager.service.planList.GetUserScrapPlanList;
import Trabook.PlanManager.service.planList.PlanListServiceInterface;
import Trabook.PlanManager.service.webclient.WebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/plans")
public class PlanListController {

    private final PlanService planService;
    private final Map<String, PlanListServiceInterface> planListServiceInterfaceMap;
    private final WebClientService webClientService;
    private final PlanRedisService planRedisService;

    //PlanListServiceInterface 인터페이스를 구현한 모든 서비스들이 자동으로 주입됨. 스프링이 자동으로 이 인터페이스를 구현한
    //모든 빈을 찾아서 리스트로 제공한다
    @Autowired
    public PlanListController(List<PlanListServiceInterface> planListService, PlanService planService, WebClientService webClientService,PlanRedisService planRedisService) {
        this.webClientService= webClientService;
        this.planRedisService = planRedisService;
        this.planService = planService;
        this.planListServiceInterfaceMap = planListService.stream().collect(Collectors.toMap(
                service -> {
                    if(service instanceof GetUserLikePlanList) return "likes";
                    if(service instanceof GetUserPlanList) return "user";
                    if(service instanceof GetUserScrapPlanList) return "scrap";
                    return "default";
                },
                service -> service //?? 이문구 문법적으로 알아보기

        ));

    }


    @ResponseBody
    @GetMapping("")
    public List<PlanListResponseDTO> getPlanList(@RequestParam(name="type") String type, @RequestHeader(name="userId") long userId) {
        PlanListServiceInterface planListService = planListServiceInterfaceMap.get(type);
        List<PlanListResponseDTO> planList = planListService.getPlanList(userId);
        log.info("{}'s plans = {}", userId, planList);
        return planList;
    }

    @ResponseBody
    @GetMapping("/general")
    public CustomPlanListDto getCustomPlans(
            @RequestParam String search,
            @RequestParam(required = false) List<String> state,
            @RequestParam(required = false) Integer numOfPeople,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false, defaultValue = "likes") String sorts,
            @RequestParam Integer pageSize,
            @RequestParam Integer pageNum,
            @RequestParam Boolean userScrapOnly,
            @RequestHeader(required = false) Integer userId) {


        List<PlanGeneralDto> customPlanList =
                planService.findCustomPlanList(search, state, numOfPeople, duration, sorts, userId, userScrapOnly);
        //return customPlanList;
        Integer totalPages = (customPlanList.size() + pageSize - 1) / pageSize;

        // 페이지 번호가 유효한지 확인 (잘못된 pageNum이면 빈 리스트와 totalPages 반환)
        if (pageNum < 0 || pageNum >= totalPages) {
            return new CustomPlanListDto(Collections.emptyList(), totalPages);
        }

        // 해당 페이지에 맞는 시작과 끝 인덱스 계산
        int startIndex = pageNum * pageSize;
        int endIndex = Math.min(startIndex + pageSize, customPlanList.size());
        // 서브리스트 반환 (페이지의 일부 요소와 전체 페이지 수)
        return new CustomPlanListDto(customPlanList.subList(startIndex, endIndex), totalPages);
    }

    @ResponseBody
    @GetMapping("/popular")
    public List<PlanListResponseDTO> getHottestPlan(@RequestHeader(value = "userId", required = false) Long userId) {
        List<PlanListResponseDTO> hottestPlan = planRedisService.getHottestPlan();
        return hottestPlan;
    }





}

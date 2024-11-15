package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.CustomPlaceListDTO;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;
import Trabook.PlanManager.service.destination.DestinationRedisService;
import Trabook.PlanManager.service.destination.DestinationService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/places")
public class DestinationListController {
    private final DestinationService destinationService;


    @Autowired
    public DestinationListController(DestinationService destinationService) {
        this.destinationService = destinationService;

    }

    @ResponseBody
    @GetMapping("/popular")
    public List<PlaceForModalDTO> getHottestPlace(@RequestHeader(value = "userId", required = false) Long userId) {
        return destinationService.getHottestPlace(userId);
    }








/*


    @ResponseBody
    @GetMapping("/popular")
    public List<PlaceForModalDTO> getHottestPlaceRedis(@RequestHeader(value="userId",required = false) Long userId) {
        return destinationRedisService.getHottestPlace(userId);


    }


 */






//    @ResponseBody
//    @GetMapping("/scrap")
//    public CustomPlaceListDTO getUserScrapPlace(@RequestHeader(name="userId") long userId,
//                                         @RequestParam Integer pageSize,
//                                         @RequestParam Integer pageNum) {
//        //return destinationService.getPlaceListByUserScrap(userId);
//        List<Place> customPlaceList = destinationService.getPlaceListByUserScrap(userId);
//        // 전체 페이지 수 계산
//        Integer totalPages = (customPlaceList.size() + pageSize - 1) / pageSize; // 올림 처리
//
//        // 페이지 번호가 유효한지 확인 (잘못된 pageNum이면 빈 리스트와 totalPages 반환)
//        if (pageNum < 0 || pageNum >= totalPages) {
//            return new CustomPlaceListDTO(Collections.emptyList(), totalPages);
//        }
//
//        // 해당 페이지에 맞는 시작과 끝 인덱스 계산
//        int startIndex = pageNum * pageSize;
//        int endIndex = Math.min(startIndex + pageSize, customPlaceList.size());
//
//        // 서브리스트 반환 (페이지의 일부 요소와 전체 페이지 수)
//        return new CustomPlaceListDTO(customPlaceList.subList(startIndex, endIndex), totalPages);
//    }

    @ResponseBody
    @GetMapping("/general")
    public CustomPlaceListDTO getUserCustomPlaceList(
            @RequestParam String search,
            @RequestParam(required = false) List<String> state,
            @RequestParam(required = false) List<String> category,
            @RequestParam String sorts,
            @RequestParam Integer pageSize,
            @RequestParam Integer pageNum,
            @RequestParam Boolean userScrapOnly,
            @RequestHeader(required = false) Integer userId) {
        log.info("/places/general");

        List<PlaceForModalDTO> customPlaceList = destinationService.getUserCustomPlaceList(search, state, category, sorts, userId, userScrapOnly);

        // 전체 페이지 수 계산
        Integer totalPages = (customPlaceList.size() + pageSize - 1) / pageSize; // 올림 처리

        // 페이지 번호가 유효한지 확인 (잘못된 pageNum이면 빈 리스트와 totalPages 반환)
        if (pageNum < 0 || pageNum >= totalPages) {
            return new CustomPlaceListDTO(Collections.emptyList(), totalPages);
        }

        // 해당 페이지에 맞는 시작과 끝 인덱스 계산
        int startIndex = pageNum * pageSize;
        int endIndex = Math.min(startIndex + pageSize, customPlaceList.size());

        // 서브리스트 반환 (페이지의 일부 요소와 전체 페이지 수)
        return new CustomPlaceListDTO(customPlaceList.subList(startIndex, endIndex), totalPages);
    }
}

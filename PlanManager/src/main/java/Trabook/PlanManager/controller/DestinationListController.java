package Trabook.PlanManager.controller;

import Trabook.PlanManager.dto.CustomPlaceListDto;
import Trabook.PlanManager.dto.DestinationDto.PlaceForModalDTO;
import Trabook.PlanManager.service.destination.DestinationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class DestinationListController {
    private final DestinationService destinationService;

    @ResponseBody
    @GetMapping("/popular")
    public ResponseEntity<List<PlaceForModalDTO>> getHottestPlace(@RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok().body(destinationService.getHottestPlace(userId));
    }


    @ResponseBody
    @GetMapping("/general")
    public CustomPlaceListDto getUserCustomPlaceList(
            @RequestParam String search,
            @RequestParam(required = false) List<String> state,
            @RequestParam(required = false) List<String> category,
            @RequestParam String sorts,
            @RequestParam Integer pageSize,
            @RequestParam Integer pageNum,
            @RequestParam Boolean userScrapOnly,
            @RequestHeader(required = false) Integer userId) {

        List<PlaceForModalDTO> customPlaceList = destinationService.getUserCustomPlaceList(search, state, category, sorts, userId, userScrapOnly);

        // 전체 페이지 수 계산
        Integer totalPages = (customPlaceList.size() + pageSize - 1) / pageSize; // 올림 처리

        // 페이지 번호가 유효한지 확인 (잘못된 pageNum이면 빈 리스트와 totalPages 반환)
        if (pageNum < 0 || pageNum >= totalPages) {
            return new CustomPlaceListDto(Collections.emptyList(), totalPages);
        }

        // 해당 페이지에 맞는 시작과 끝 인덱스 계산
        int startIndex = pageNum * pageSize;
        int endIndex = Math.min(startIndex + pageSize, customPlaceList.size());

        // 서브리스트 반환 (페이지의 일부 요소와 전체 페이지 수)
        return new CustomPlaceListDto(customPlaceList.subList(startIndex, endIndex), totalPages);
    }
}

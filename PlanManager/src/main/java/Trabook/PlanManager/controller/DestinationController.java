package Trabook.PlanManager.controller;

import Trabook.PlanManager.dto.GetPlaceResponseDto;
import Trabook.PlanManager.dto.PlaceScrapRequestDTO;
import Trabook.PlanManager.response.ResponseMessage;
import Trabook.PlanManager.service.destination.DestinationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/place")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }


    @ResponseBody
    @GetMapping("")
    public ResponseEntity<?> getPlaceByPlaceId(@RequestParam("placeId") Long placeId, @RequestHeader(value = "userId",required = false)Long userId ){
        if(placeId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("placeId 없음");

        GetPlaceResponseDto getPlaceResponseDto = destinationService.getPlaceModalByPlaceId(placeId);
        getPlaceResponseDto.setIsScrapped(isScrapped(placeId, userId));
        return ResponseEntity.ok(getPlaceResponseDto);
    }

    private boolean isScrapped(Long placeId, Long userId) {
        if(userId == null)
            return false;
        else
            return destinationService.isScrapPlace(placeId,userId);
    }


    @ResponseBody
    @PostMapping("/scrap")
    public ResponseEntity<String> addPlaceScrap(@RequestBody PlaceScrapRequestDTO placeScrapRequestDTO, @RequestHeader("userId")Long userId){
        if(userId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("userId 없음");
        try {
            destinationService.addPlaceScrap(userId, placeScrapRequestDTO.getPlaceId());
            return ResponseEntity.ok("스크랩 성공");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 여행지 없음");
        }
    }

    @ResponseBody
    @DeleteMapping("/scrap")
    public  ResponseEntity<String> deletePlaceScrap(@RequestBody PlaceScrapRequestDTO placeScrapRequestDTO,@RequestHeader("userId")Long userId){
        if(userId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("userId 없음");
        try {
            destinationService.deletePlaceScrap(userId, placeScrapRequestDTO.getPlaceId());
            return ResponseEntity.ok("스크랩 삭제 성공");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

}

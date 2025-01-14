package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.destination.DestinationReactionDto;
import Trabook.PlanManager.domain.destination.PlaceForModalAddPictureDTO;
import Trabook.PlanManager.domain.destination.PlaceForModalDTO;
import Trabook.PlanManager.domain.destination.PlaceScrapRequestDTO;
import Trabook.PlanManager.response.ResponseMessage;
import Trabook.PlanManager.service.destination.DestinationService;
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
    public ResponseEntity<PlaceForModalAddPictureDTO> getPlaceByPlaceId(@RequestParam("placeId") long placeId, @RequestHeader(value = "userId",required = false)Long userId ){
        PlaceForModalAddPictureDTO result = destinationService.getPlaceModalByPlaceId(placeId);

        if(userId != null) {
            //System.out.println(destinationService.isScrapPlace(placeId,userId));
            //System.out.println("user Id = " + userId);
            result.getPlace().setIsScrapped(destinationService.isScrapPlace(placeId,userId));
        } else{
            result.getPlace().setIsScrapped(false);
        }
        return ResponseEntity.ok(result);
    }



    @ResponseBody
    @PostMapping("/scrap")
    public ResponseEntity<ResponseMessage> addPlaceScrap(@RequestBody PlaceScrapRequestDTO placeScrapRequestDTO, @RequestHeader("userId")long userId){

        String message = destinationService.addPlaceScrap(userId, placeScrapRequestDTO.getPlaceId());

        if (Objects.equals(message, "no place exists")){
            return new ResponseEntity<>(new ResponseMessage("no plan exists"), HttpStatus.NOT_FOUND);
        }else if(Objects.equals(message, "already scrap error")){
            return new ResponseEntity<>(new ResponseMessage("already scrap error"), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(new ResponseMessage(message));
    }

    @ResponseBody
    @DeleteMapping("/scrap")
    public  ResponseEntity<ResponseMessage> deletePlaceScrap(@RequestBody PlaceScrapRequestDTO placeScrapRequestDTO,@RequestHeader("userId")long userId){
        String message = destinationService.deletePlaceScrap(userId, placeScrapRequestDTO.getPlaceId());
        return ResponseEntity.ok(new ResponseMessage(message));
    }


}

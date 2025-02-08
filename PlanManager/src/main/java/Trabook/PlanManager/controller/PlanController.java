package Trabook.PlanManager.controller;

import Trabook.PlanManager.domain.plan.*;

import Trabook.PlanManager.dto.PlanCreateRequestDto;
import Trabook.PlanManager.dto.PlanCreateResponseDto;
import Trabook.PlanManager.dto.PlanIdDTO;
import Trabook.PlanManager.response.*;

import Trabook.PlanManager.service.PlanService;
import Trabook.PlanManager.service.file.FileUploadService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Objects;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {


    private final PlanService planService;
    private final FileUploadService fileUploadService;

    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<PlanCreateResponseDto> createPlan(@RequestBody PlanCreateRequestDto planCreateRequestDTO, @RequestHeader("userId") Long userId) throws FileNotFoundException {
        Long planId = planService.createPlan(planCreateRequestDTO,userId);
        String fileName = fileUploadService.uploadDefaultImage(planId);
        PlanCreateResponseDto planCreateResponseDTO = new PlanCreateResponseDto(planId,"create complete","https://storage.googleapis.com/trabook-20240822/"+fileName);
        return new ResponseEntity<>(planCreateResponseDTO, HttpStatus.OK);

    }

    @ResponseBody
    @PatchMapping("/update")
    public ResponseEntity<PlanUpdateResponseDTO> updatePlan(@RequestPart("plan") TotalPlan totalPlan,
                                                            @RequestPart(value = "image",required = false) MultipartFile image) {

        long planId = planService.updatePlan(totalPlan);
        if(planId == 0)
            return new ResponseEntity<>(new PlanUpdateResponseDTO(-1,"no plan exists"), HttpStatus.NOT_FOUND);
        try {
            if(!image.isEmpty()) {
                fileUploadService.uploadPlanImage(image, planId);
            }
            else
                System.out.println("no image");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PlanUpdateResponseDTO planUpdateResponseDTO = new PlanUpdateResponseDTO(planId,"update complete");

        return new ResponseEntity<>(planUpdateResponseDTO,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("")
    public ResponseEntity<?> getPlanByPlanId(@RequestParam("planId")Long planId, @RequestHeader(value = "userId", required = false) Long userId) throws InterruptedException {
        if(planId == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("planId 없음");
        }
        PlanResponseDTO planResponseDTO = planService.handleTotalPlanRequest(planId, userId);
        return ResponseEntity.ok(planResponseDTO);
    }


    @ResponseBody
    @PostMapping("/like")
    public ResponseEntity<ResponseMessage> likePlan(@RequestBody PlanIdDTO planIdDTO, @RequestHeader("userId") long userId) {

        String message = planService.likePlan(planIdDTO.getPlanId(),userId);
        if (Objects.equals(message,"like already exists" )){
            return new ResponseEntity<>(new ResponseMessage("like already exists"), HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok(new ResponseMessage(message));

    }


    @ResponseBody
    @PostMapping("/scrap")
    public ResponseEntity<ResponseMessage> scrapPlan(@RequestBody PlanIdDTO planIdDTO, @RequestHeader(value = "userId") long userId) {

        String message = planService.scrapPlan(planIdDTO.getPlanId(),userId);
        if (Objects.equals(message, "no plan exists")){
            return new ResponseEntity<>(new ResponseMessage("no plan exists"), HttpStatus.NOT_FOUND);
        }else if(Objects.equals(message, "already scrap error")){
            return new ResponseEntity<>(new ResponseMessage("already scrap error"), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(new ResponseMessage(message));

    }


    @ResponseBody
    @DeleteMapping("")
    public ResponseEntity<?> deletePlan(@RequestParam("planId") long planId,@RequestHeader("userId") Long userId) {
        if(userId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 필요");
        try {
            planService.deletePlan(planId,userId);
            return ResponseEntity.ok("계획 삭제 성공");
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @ResponseBody
    @DeleteMapping("/like")
    public ResponseEntity<ResponseMessage> deleteLike(@RequestHeader("userId") long userId, @RequestParam("planId") long planId){
        String message = planService.deleteLike(userId, planId);
        return ResponseEntity.ok(new ResponseMessage(message));

    }

    @ResponseBody
    @DeleteMapping("/scrap")
    public ResponseEntity<ResponseMessage> deleteScrap(@RequestHeader("userId") long userId, @RequestParam("planId") long planId) {
        String message = planService.deleteScrap(userId, planId);
        return ResponseEntity.ok(new ResponseMessage(message));
    }


}

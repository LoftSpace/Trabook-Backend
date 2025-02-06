package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.*;
import Trabook.PlanManager.dto.DestinationReactionDto;
import Trabook.PlanManager.dto.GetPlaceResponseDto;
import Trabook.PlanManager.dto.PlaceForModalDTO;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;



    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public List<Place> getPlaceListByCity(long cityId) {
        return destinationRepository.findPlaceListByCity(cityId);
    }

    public List<Place> getPlaceListByUserScrap(long userId) {
        return destinationRepository.findPlaceListByUserScrap(userId);
    }

    @Transactional
    public void addPlaceScrap(Long userId, Long placeId) throws EntityNotFoundException {
        destinationRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Place not found"));
        destinationRepository.addPlaceScrap(userId, placeId);
    }

    @Transactional
    public void deletePlaceScrap(long userId, long placeId)  throws RuntimeException{
        destinationRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new EntityNotFoundException("여행지 없음"));

        if(destinationRepository.deletePlaceScrap(userId,placeId)==1) {
            destinationRepository.scrapDown(placeId);
        }
        else
            throw new EntityNotFoundException("스크랩 없음");
    }


    @Transactional
    public List<PlaceForModalDTO> getUserCustomPlaceList(String search,
                                                         List<String> state,
                                                         List<String> category,
                                                         String sorts,
                                                         Integer userId,
                                                         Boolean userScrapOnly) {
        return destinationRepository.findCustomPlaceList(search, state, category, sorts, userId, userScrapOnly);
    }


    public boolean isScrapPlace(long placeId,long userId) {
        return destinationRepository.isScrapped(placeId,userId);
    }

    public List<PlaceForModalDTO> getHottestPlace(Long userId){
        List<Place> hottestPlaceList = destinationRepository.findHottestPlaceList();
        List<PlaceForModalDTO> hottestPlaceModalList = new ArrayList<>();

        for(Place place : hottestPlaceList){
            hottestPlaceModalList.add(changeToModal(userId,place));
        }
        return hottestPlaceModalList;
    }

    private PlaceForModalDTO changeToModal(Long userId, Place place) {
        boolean isScrapped = checkScrap(userId, place.getPlaceId());
        return PlaceForModalDTO.builder()
                .place(place)
                .comments(destinationRepository.findCommentsByPlaceId(place.getPlaceId()))
                .isScrapped(isScrapped)
                .build();
    }

    private boolean checkScrap(Long userId, long placeId) {
        if(userId == null) {
            return false;
        }else {
            return destinationRepository.isScrapped(placeId,userId);
        }
    }

    public GetPlaceResponseDto getPlaceModalByPlaceId(Long placeId) {
        Place place = destinationRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new EntityNotFoundException("여행지 없음"));
        List<PlaceComment> comments = destinationRepository.findCommentsByPlaceId(placeId);
        List<String> photos = destinationRepository.findPhotosByPlaceId(placeId);
        return new GetPlaceResponseDto(place,comments,photos);
    }
}

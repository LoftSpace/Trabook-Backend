package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.*;
import Trabook.PlanManager.dto.DestinationDto.GetPlaceResponseDto;
import Trabook.PlanManager.dto.DestinationDto.PlaceForModalDTO;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
                .orElseThrow(() -> new EntityNotFoundException("여행지 없음"));
        destinationRepository.addPlaceScrap(userId, placeId);
        destinationRepository.scrapUp(placeId);
    }

    @Transactional
    public void deletePlaceScrap(long userId, long placeId)  throws RuntimeException{
        destinationRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new EntityNotFoundException("여행지 없음"));

        if(destinationRepository.deletePlaceScrap(userId,placeId)==1)
            destinationRepository.scrapDown(placeId);
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
        return GetPlaceResponseDto.builder()
                .place(place)
                .comments(comments)
                .photos(photos)
                .build();
    }
}

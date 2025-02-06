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
    public String addPlaceReaction(DestinationReactionDto destinationReactionDto) {

        String reactionType = destinationReactionDto.getReactionType();
        long userId = destinationReactionDto.getUserId();
        long placeId = destinationReactionDto.getPlaceId();

        if(reactionType.equals("LIKE")) {
            if(destinationRepository.findByPlaceId(placeId).isPresent()) {
                try {
                    destinationRepository.addPlaceLike(userId, placeId);
                    return "like complete";
                } catch(DataAccessException e) {
                    if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                        return "already liked";
                    }
                    return "error accessing db";
                }
            }
            return "no place";
        }
        else if(reactionType.equals("SCRAP")) {
            if(destinationRepository.findByPlaceId(placeId).isPresent()){
                try {
                    destinationRepository.addPlaceScrap(userId, placeId);
                    return "scrap complete";
                } catch(DataAccessException e) {
                    if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                        return "already scrapped";
                    }
                    return "error accessing db";
                }
            }
            return "no place";
        }
        else
            return "type invalid";

    }
    @Transactional
    public String deletePlaceReaction(DestinationReactionDto destinationReactionDto) {
        String reactionType = destinationReactionDto.getReactionType();
        long userId = destinationReactionDto.getUserId();
        long placeId = destinationReactionDto.getPlaceId();

        if(reactionType.equals("LIKE")) {
            if(destinationRepository.deletePlaceLike(userId,placeId)==1) {
                destinationRepository.likeDown(placeId);
                return "delete like complete";
            }
            else
                return "can't delete null";
        }
        else if(reactionType.equals("SCRAP")) {
            if(destinationRepository.deletePlaceScrap(userId,placeId)==1) {
                destinationRepository.scrapDown(placeId);
                return "delete scrap complete";
            }
            else
                return "can't delete null";
        }
        else
            return "type invalid";
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

    @Transactional
    public boolean isScrapPlace(long placeId,long userId) {
        return destinationRepository.isScrapped(placeId,userId);
    }

    public List<PlaceForModalDTO> getHottestPlace(Long userId){
        List<Place> top10Places = destinationRepository.findHottestPlaceList();
        List<PlaceForModalDTO> result = new ArrayList<>();

        for(Place place : top10Places){
            if(userId == null) {
                place.setIsScrapped(false);
            }else {
                place.setIsScrapped(destinationRepository.isScrapped(place.getPlaceId(), userId));
            }

            List<PlaceComment> comments = destinationRepository.findCommentsByPlaceId(place.getPlaceId());

            result.add(new PlaceForModalDTO(place, comments));
        }
        return result;
    }


    public Optional<Place> getPlaceByPlaceId(long placeId) {
        return destinationRepository.findByPlaceId(placeId);
    }

    public GetPlaceResponseDto getPlaceModalByPlaceId(Long placeId)  {
        Place place = destinationRepository.findByPlaceId(placeId).get();
        List<PlaceComment> comments = destinationRepository.findCommentsByPlaceId(placeId);
        List<String> photos = destinationRepository.findPhotosByPlaceId(placeId);

        return new GetPlaceResponseDto(place,comments,photos);
    }
}

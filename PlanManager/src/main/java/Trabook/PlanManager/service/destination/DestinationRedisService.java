package Trabook.PlanManager.service.destination;

import org.springframework.stereotype.Service;

@Service
public class DestinationRedisService {

/*
    private final DestinationRepository destinationRepository;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public DestinationRedisService(@Qualifier("topRedisTemplate")RedisTemplate<String, String> redisTemplate, DestinationRepository destinationRepository) {
        this.redisTemplate = redisTemplate;
        this.destinationRepository = destinationRepository;
    }

    private ObjectMapper objectMapper = new ObjectMapper();


    public List<PlaceForModalDTO> getHottestPlace(Long userId){
        objectMapper.registerModule(new SimpleModule().addDeserializer(Point.class, new PointDeserializer()));

        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
        Set<String> topPlaces = zsetOps.reverseRange("topPlaces", 0, 9);

        List<Place> top10Places = new ArrayList<>();
        List<PlaceForModalDTO> result = new ArrayList<>();
        try {
            for (String jsonPlace : topPlaces) {
                //System.out.println(jsonPlace);
                Place place = objectMapper.readValue(jsonPlace, Place.class);
                top10Places.add(place);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

 */
}

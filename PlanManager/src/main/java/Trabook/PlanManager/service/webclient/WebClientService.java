package Trabook.PlanManager.service.webclient;

import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.domain.webclient.UserListDTO;
import Trabook.PlanManager.domain.webclient.userInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebClientService {

    @Autowired
    WebClient webClient;

    public User getUserInfoBlocking(long userId) {

        URI uri = UriComponentsBuilder
                .fromUriString("http://35.216.63.121:4060")
                .path("/auth/fetch-user")
                .queryParam("userId", userId)
                .encode(Charset.defaultCharset())
                .build()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<userInfoDTO> result = restTemplate.getForEntity(uri, userInfoDTO.class);
        System.out.println(result.toString());
        return result.getBody().getUser();
    }

    public List<User> getUserInfoListBlocking(List<Long> userIdList) {
        // User ID 리스트를 문자열로 변환
        String userIdsString = userIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // URI 구성
        URI uri = UriComponentsBuilder
                .fromUriString("http://35.216.63.121:4060")
                .path("/auth/fetch-users")
                .queryParam("userIdList", userIdsString)
                .encode(Charset.defaultCharset())
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        UserListDTO forObject = restTemplate.getForObject(uri, UserListDTO.class);
        return forObject.getUsers();


    }
}

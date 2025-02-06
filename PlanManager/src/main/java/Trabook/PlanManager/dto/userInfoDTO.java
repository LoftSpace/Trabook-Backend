package Trabook.PlanManager.dto;

import Trabook.PlanManager.domain.user.User;
import lombok.Getter;

@Getter
public class userInfoDTO {
    private User user;

    private userInfoDTO() {}

    public userInfoDTO(User user) {
        this.user = user;
    }


}

package Trabook.PlanManager.dto;

import Trabook.PlanManager.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserListDTO {
    private List<User> users;

    public UserListDTO() {}
    public UserListDTO(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "UserListDTO{" +
                "users=" + users +
                '}';
    }
}

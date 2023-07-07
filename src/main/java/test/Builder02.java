package test;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
class User {
    private Integer id;
    private String username;
    private String password;
    private Boolean status; // true 활동, false 휴먼
    private Timestamp createdAt; // 가입날짜 now()

    public User(Integer id, String username, String password, Boolean status, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
    }

    public User(String username, String password, Boolean status) {
        this.username = username;
        this.password = password;
        this.status = status;
    }
}

public class Builder02 {
    public static void main(String[] args) {
        // given
        String username = "ssar";
        String password = "1234";

        User user = new User(
                null,
                password,
                username,
                true,
                null
        );

        User user2 = new User(
                username,
                password,
                true
        );
    }
}

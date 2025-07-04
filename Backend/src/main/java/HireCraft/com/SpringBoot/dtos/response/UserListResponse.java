package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class UserListResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status;
    private String city;
    private String state;
    private String country;

    public UserListResponse(Long id, String firstName, String lastName, String email, String phoneNumber, String city, String state, String country, String name) {
    }
}


package com.lms.userapi.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}

package com.gmail.buer2012.payload;

import lombok.Data;

@Data
public class SignUpRequest {

    private String username;
    private String email;
    private String password;

}

package com.gmail.buer2012.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
public class SignInRequest {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;

}

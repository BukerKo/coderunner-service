package com.gmail.buer2012.payload;

import lombok.Data;
import lombok.NonNull;


@Data
public class SignInRequest {

    @NonNull
    private String usernameOrEmail;

    @NonNull
    private String password;

}

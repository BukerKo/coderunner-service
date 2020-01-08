package com.gmail.buer2012.payload;

import com.gmail.buer2012.entity.RoleName;
import lombok.Data;
import lombok.NonNull;

@Data
public class JwtAuthenticationResponse {

    @NonNull
    private String accessToken;

    @NonNull
    private RoleName role;
    
    @NonNull
    private String username;

    private String tokenType = "Bearer";
    
}

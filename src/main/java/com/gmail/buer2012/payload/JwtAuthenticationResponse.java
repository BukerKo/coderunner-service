package com.gmail.buer2012.payload;

import com.gmail.buer2012.entity.AuthProvider;
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

    @NonNull
    private AuthProvider authProvider;

    private String tokenType = "Bearer";
    
}

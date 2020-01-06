package com.gmail.buer2012.payload;

import lombok.*;

@Data
public class ApiResponse {

    @NonNull
    private Boolean success;
    
    @NonNull
    private String message;
    
}

package com.gmail.buer2012.payload;

import lombok.Data;

@Data
public class RestorePasswordRequest {

  private String email;
  private String token;
  private String password;
}

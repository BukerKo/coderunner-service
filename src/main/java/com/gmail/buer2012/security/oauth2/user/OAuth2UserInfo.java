package com.gmail.buer2012.security.oauth2.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public abstract class OAuth2UserInfo {
    
    @Getter
    protected Map<String, Object> attributes;
    
    public abstract String getId();
    
    public abstract String getName();
    
    public abstract String getEmail();
    
}
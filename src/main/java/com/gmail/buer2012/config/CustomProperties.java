package com.gmail.buer2012.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.temporary.dir")
public class CustomProperties {
    
    @Getter
    @Setter
    private String temporaryDir = "temporary";
}

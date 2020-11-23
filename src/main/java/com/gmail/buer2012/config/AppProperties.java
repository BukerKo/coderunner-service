package com.gmail.buer2012.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private final Auth auth = new Auth();

    private String temporaryDir = "temporary";
    private Integer timeout = 5;
    private String frontUrl;

    @Data
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;
    }
}

package com.gmail.buer2012.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "settings")
@Data
public class CustomProperties {

    private String temporaryDir = "temporary";

    private String frontUrl;

    private Integer timeout = 5;
}

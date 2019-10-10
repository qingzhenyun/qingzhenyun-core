package com.qingzhenyun.userfile.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {
    private String separator;

    public String getSeparator() {
        return this.separator;
    }

    @ConfigurationProperties("user.test.separator")
    public void setSeparator(String separator) {
        this.separator = separator;
    }
}

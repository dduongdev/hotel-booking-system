package com.dduongdev.hotel.security.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "app.security")
@Getter 
@Setter
public class SecurityProperties {
    private List<String> skipActivatedUrls;
}
package com.project.notificationService.models;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailProps {

    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties;

}

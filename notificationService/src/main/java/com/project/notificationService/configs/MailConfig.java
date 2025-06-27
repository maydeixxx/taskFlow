package com.project.notificationService.configs;

import com.project.notificationService.models.MailProps;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailProps props;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(props.getHost());
        mailSender.setUsername(props.getUsername());
        mailSender.setPort(props.getPort());
        mailSender.setPassword(props.getPassword());
        mailSender.setJavaMailProperties(props.getProperties());
        return mailSender;
    }

}

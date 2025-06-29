package com.project.notificationService.services;

import com.project.notificationService.models.NotificationEvent;
import freemarker.template.Configuration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final Configuration configuration;

    public void sendMail(NotificationEvent event) {
        switch (event.getEventType()) {
            case "newTask" -> sendNewTaskEmail(event);
            case "reminder" -> sendReminderMail(event);
            case "registration" -> sendRegMail(event);
            case "newProject" -> sendNewProjectEmail(event);
            default -> throw new IllegalArgumentException("Unknown type of event");
        }
        log.info("Sent messages");
    }

    private void sendRegMail(NotificationEvent event) {
        String username = event.getUsername();
        String email = event.getEmail();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setSubject(String.format("Thank you for registration on taskFlow, %s!", username));
            helper.setTo(email);
            String emailContent = getRegistrationEmailContent(event);
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send registration email", e);
        }
    }

    private void sendReminderMail(NotificationEvent event) {
        String username = event.getUsername();
        String email = event.getEmail();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setSubject(String.format("%s, you have task to do in 12 hours!", username));
            helper.setTo(email);
            String emailContent = getReminderEmailContent(event);
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send reminder email", e);
        }
    }

    private void sendNewTaskEmail(NotificationEvent event) {
        String username = event.getUsername();
        String email = event.getEmail();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setSubject(String.format("You have new task, %s!", username));
            helper.setTo(email);
            String content = getNewTaskEmailContent(event);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendNewProjectEmail(NotificationEvent event) {
        String email = event.getEmail();
        String username = event.getUsername();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setSubject(String.format("You assigned to new project, %s!", username));
            helper.setTo(email);
            String content = getNewProjectEmailContent(event);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getNewTaskEmailContent(NotificationEvent event) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("name", event.getUsername());
        model.put("title", event.getTitle());
        model.put("taskId", event.getTaskId());
        try {
            configuration.getTemplate("newTask.ftlh")
                    .process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getNewProjectEmailContent(NotificationEvent event) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("name", event.getUsername());
        model.put("projectId", event.getProjectId());
        try {
            configuration.getTemplate("newProject.ftlh")
                    .process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getRegistrationEmailContent(NotificationEvent event) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("name", event.getUsername());
        try {
            configuration.getTemplate("register.ftlh")
                    .process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getReminderEmailContent(NotificationEvent event) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("name", event.getUsername());
        model.put("title", event.getTitle());
        try {
            configuration.getTemplate("reminder.ftlh")
                    .process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

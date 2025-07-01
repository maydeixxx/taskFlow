package com.project.notificationService.services;

import com.project.notificationService.models.NotificationEvent;
import freemarker.template.Configuration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final Configuration configuration;
    private final Map<Long, NotificationEvent> reminders = new ConcurrentHashMap<>();

    public void sendMail(NotificationEvent event) {
        switch (event.getEventType()) {
            case "newTask" -> {
                sendNewTaskEmail(event);
                reminders.put(event.getTaskId(), event);
            }
            case "reminder" -> sendReminderMail(event);
            case "registration" -> sendRegMail(event);
            case "newProject" -> sendNewProjectEmail(event);
            default -> throw new IllegalArgumentException("Unknown type of event");
        }
        log.info("Sent message for event type: {}", event.getEventType());
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
            log.error("Failed to send registration email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send registration email", e);
        }
    }

    private void sendReminderMail(NotificationEvent event) {
        String username = event.getUsername();
        String email = event.getEmail();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            long hoursRemaining = Duration.between(LocalDateTime.now(), event.getDeadline()).toHours();
            helper.setSubject(String.format("%s, you have %d hours to complete the task!", username, hoursRemaining));
            helper.setTo(email);
            String emailContent = getReminderEmailContent(event);
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Failed to send reminder email to {}: {}", event.getEmail(), e.getMessage());
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
            log.error("Failed to send new task email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send new task email", e);
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
        } catch (MessagingException e) {
            log.error("Failed to send new project email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send new project email", e);
        }
    }

    private String getNewTaskEmailContent(NotificationEvent event) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("name", event.getUsername());
        model.put("title", event.getTitle() != null ? event.getTitle() : "Untitled Task");
        model.put("taskId", event.getTaskId());
        try {
            configuration.getTemplate("newTask.ftlh").process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            log.error("Failed to process new task email template: {}", e.getMessage());
            throw new RuntimeException("Failed to process new task email template", e);
        }
    }

    private String getNewProjectEmailContent(NotificationEvent event) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("name", event.getUsername());
        model.put("projectId", event.getProjectId());
        try {
            configuration.getTemplate("newProject.ftlh").process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            log.error("Failed to process new project email template: {}", e.getMessage());
            throw new RuntimeException("Failed to process new project email template", e);
        }
    }

    private String getRegistrationEmailContent(NotificationEvent event) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("name", event.getUsername());
        try {
            configuration.getTemplate("register.ftlh").process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            log.error("Failed to process registration email template: {}", e.getMessage());
            throw new RuntimeException("Failed to process registration email template", e);
        }
    }

    private String getReminderEmailContent(NotificationEvent event) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        Duration remaining = Duration.between(LocalDateTime.now(), event.getDeadline());
        long hoursRemaining = remaining.toHours();
        model.put("name", event.getUsername());
        model.put("title", event.getTitle());
        model.put("hoursRemaining", hoursRemaining);
        try {
            configuration.getTemplate("reminder.ftlh").process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            log.error("Failed to process reminder email template: {}", e.getMessage());
            throw new RuntimeException("Failed to process reminder email template", e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void checkReminders() {
        log.info("cached tasks for reminder: {}", reminders);
        LocalDateTime now = LocalDateTime.now();
        reminders.entrySet().removeIf(entry -> {
            Long taskId = entry.getKey();
            NotificationEvent event = entry.getValue();

            Duration remaining = Duration.between(now, event.getDeadline());
            long hoursRemaining = remaining.toHours();
            log.info("remaining: {} || HoursRemaining: {}", remaining, hoursRemaining);

            if (hoursRemaining <= 12 && !event.isReminderSent() ) {
                event.setEventType("reminder");
                sendReminderMail(event);
                event.setReminderSent(true);
                log.info("Sent reminder for taskId {} to {}, remaining {} hours", taskId, event.getEmail(), hoursRemaining);
                log.info("removed task {}", taskId);
                return true;
            } else if (hoursRemaining <= 0) {
                log.info("Removed expired taskId {} from reminders", taskId);
                return true;
            }
            return false;
        });
    }
}
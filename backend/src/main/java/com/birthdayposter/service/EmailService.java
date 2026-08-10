package com.birthdayposter.service;

import com.birthdayposter.entity.GenerationHistory;
import com.birthdayposter.entity.Person;
import com.birthdayposter.entity.User;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    /**
     * Send a single birthday poster email to the app user (account holder).
     */
    public void sendPosterEmail(User user, Person person, GenerationHistory history, byte[] posterBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, fromName));
            helper.setTo(user.getEmail());
            helper.setSubject("🎂 Happy Birthday " + person.getName() + "! — GreetStudio Poster Ready");

            String htmlContent = buildSinglePosterHtml(person, history);
            helper.setText(htmlContent, true);

            // Attach the poster PNG
            if (posterBytes != null && posterBytes.length > 0) {
                DataSource dataSource = new ByteArrayDataSource(posterBytes, "image/png");
                helper.addAttachment("birthday_poster_" + person.getName().replaceAll("\\s+", "_") + ".png", dataSource);
            }

            mailSender.send(message);
            log.info("Birthday poster email sent to {} for person: {}", user.getEmail(), person.getName());

        } catch (Exception e) {
            log.error("Failed to send poster email to {} for person: {}", user.getEmail(), person.getName(), e);
        }
    }

    /**
     * Send a daily digest email with all generated posters for today.
     */
    public void sendDailyDigestEmail(User user, List<GenerationHistory> todayPosters) {
        if (todayPosters == null || todayPosters.isEmpty()) {
            log.info("No posters to send in daily digest for user: {}", user.getEmail());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMultipart multipart = new MimeMultipart("mixed");

            // HTML body part
            MimeBodyPart htmlPart = new MimeBodyPart();
            String htmlContent = buildDailyDigestHtml(todayPosters);
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            // Attach each poster from its URL
            int index = 1;
            for (GenerationHistory history : todayPosters) {
                if (history.getPosterUrl() != null && !history.getPosterUrl().isBlank()) {
                    try {
                        byte[] posterBytes = downloadImage(history.getPosterUrl());
                        if (posterBytes != null) {
                            MimeBodyPart attachmentPart = new MimeBodyPart();
                            String personName = history.getPerson().getName().replaceAll("\\s+", "_");
                            DataSource ds = new ByteArrayDataSource(posterBytes, "image/png");
                            attachmentPart.setDataHandler(new DataHandler(ds));
                            attachmentPart.setFileName("poster_" + personName + ".png");
                            multipart.addBodyPart(attachmentPart);
                        }
                    } catch (Exception e) {
                        log.warn("Could not attach poster for {}: {}", history.getPerson().getName(), e.getMessage());
                    }
                }
                index++;
            }

            message.setFrom(new InternetAddress(fromEmail, fromName));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(user.getEmail()));
            message.setSubject("🎉 GreetStudio Daily Birthday Report — " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            message.setContent(multipart);

            mailSender.send(message);
            log.info("Daily digest email sent to {} with {} poster(s)", user.getEmail(), todayPosters.size());

        } catch (Exception e) {
            log.error("Failed to send daily digest email to {}", user.getEmail(), e);
        }
    }

    private byte[] downloadImage(String imageUrl) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            return in.readAllBytes();
        } catch (Exception e) {
            log.warn("Failed to download image from URL: {}", imageUrl);
            return null;
        }
    }

    // ─── HTML Templates ──────────────────────────────────────────────

    private String buildSinglePosterHtml(Person person, GenerationHistory history) {
        String posterImageTag = "";
        if (history.getPosterUrl() != null && !history.getPosterUrl().isBlank()) {
            posterImageTag = "<img src=\"" + history.getPosterUrl() +
                    "\" alt=\"Birthday Poster\" style=\"max-width:100%; border-radius:12px; margin:20px 0;\" />";
        }

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8" />
                </head>
                <body style="margin:0; padding:0; background-color:#0f172a; font-family:'Segoe UI',Arial,sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#0f172a; padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background:linear-gradient(135deg,#1e293b,#0f172a); border:1px solid #334155; border-radius:16px; overflow:hidden;">
                                    <!-- Header -->
                                    <tr>
                                        <td style="background:linear-gradient(135deg,#6366f1,#8b5cf6); padding:30px; text-align:center;">
                                            <h1 style="color:#fff; margin:0; font-size:28px;">🎂 Happy Birthday!</h1>
                                            <p style="color:#e0e7ff; margin:8px 0 0; font-size:16px;">A poster has been generated for <strong>%s</strong></p>
                                        </td>
                                    </tr>
                                    <!-- Poster Preview -->
                                    <tr>
                                        <td style="padding:30px; text-align:center;">
                                            %s
                                        </td>
                                    </tr>
                                    <!-- Wish -->
                                    <tr>
                                        <td style="padding:0 30px 20px;">
                                            <div style="background:#1e293b; border:1px solid #334155; border-radius:12px; padding:20px;">
                                                <p style="color:#94a3b8; margin:0 0 8px; font-size:12px; text-transform:uppercase; letter-spacing:1px;">Birthday Wish</p>
                                                <p style="color:#e2e8f0; margin:0; font-size:15px; line-height:1.6;">%s</p>
                                            </div>
                                        </td>
                                    </tr>
                                    <!-- Footer -->
                                    <tr>
                                        <td style="padding:20px 30px; border-top:1px solid #1e293b; text-align:center;">
                                            <p style="color:#64748b; margin:0; font-size:13px;">
                                                Generated by <strong style="color:#818cf8;">GreetStudio</strong> • The poster is also attached to this email.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(
                escapeHtml(person.getName()),
                posterImageTag,
                escapeHtml(history.getWishText() != null ? history.getWishText() : "Happy Birthday!")
        );
    }

    private String buildDailyDigestHtml(List<GenerationHistory> posters) {
        StringBuilder posterRows = new StringBuilder();
        for (GenerationHistory h : posters) {
            Person p = h.getPerson();
            String status = "SUCCESS".equals(h.getStatus()) ? "✅" : "❌";
            posterRows.append("""
                    <tr>
                        <td style="padding:12px 16px; color:#e2e8f0; border-bottom:1px solid #1e293b;">%s %s</td>
                        <td style="padding:12px 16px; color:#94a3b8; border-bottom:1px solid #1e293b;">%s</td>
                        <td style="padding:12px 16px; color:#94a3b8; border-bottom:1px solid #1e293b;">%s</td>
                        <td style="padding:12px 16px; border-bottom:1px solid #1e293b;">
                            %s
                        </td>
                    </tr>
                    """.formatted(
                    status,
                    escapeHtml(p.getName()),
                    escapeHtml(p.getDepartment() != null ? p.getDepartment() : "—"),
                    escapeHtml(h.getWishText() != null ? truncate(h.getWishText(), 60) : "—"),
                    h.getPosterUrl() != null && !h.getPosterUrl().isBlank()
                            ? "<a href=\"" + h.getPosterUrl() + "\" style=\"color:#818cf8; text-decoration:none;\">View Poster</a>"
                            : "<span style=\"color:#ef4444;\">Failed</span>"
            ));
        }

        long successCount = posters.stream().filter(h -> "SUCCESS".equals(h.getStatus())).count();
        long failedCount = posters.size() - successCount;

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8" />
                </head>
                <body style="margin:0; padding:0; background-color:#0f172a; font-family:'Segoe UI',Arial,sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#0f172a; padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table width="700" cellpadding="0" cellspacing="0" style="background:#0f172a; border:1px solid #334155; border-radius:16px; overflow:hidden;">
                                    <!-- Header -->
                                    <tr>
                                        <td style="background:linear-gradient(135deg,#6366f1,#8b5cf6); padding:30px; text-align:center;">
                                            <h1 style="color:#fff; margin:0; font-size:26px;">🎉 Daily Birthday Report</h1>
                                            <p style="color:#e0e7ff; margin:8px 0 0; font-size:15px;">%s</p>
                                        </td>
                                    </tr>
                                    <!-- Stats -->
                                    <tr>
                                        <td style="padding:24px 30px;">
                                            <table width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td width="33%%" style="text-align:center; padding:16px; background:#1e293b; border-radius:12px;">
                                                        <p style="color:#6366f1; font-size:28px; font-weight:bold; margin:0;">%d</p>
                                                        <p style="color:#94a3b8; font-size:13px; margin:4px 0 0;">Total</p>
                                                    </td>
                                                    <td width="4%%"></td>
                                                    <td width="33%%" style="text-align:center; padding:16px; background:#1e293b; border-radius:12px;">
                                                        <p style="color:#22c55e; font-size:28px; font-weight:bold; margin:0;">%d</p>
                                                        <p style="color:#94a3b8; font-size:13px; margin:4px 0 0;">Success</p>
                                                    </td>
                                                    <td width="4%%"></td>
                                                    <td width="33%%" style="text-align:center; padding:16px; background:#1e293b; border-radius:12px;">
                                                        <p style="color:#ef4444; font-size:28px; font-weight:bold; margin:0;">%d</p>
                                                        <p style="color:#94a3b8; font-size:13px; margin:4px 0 0;">Failed</p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <!-- Table -->
                                    <tr>
                                        <td style="padding:0 30px 30px;">
                                            <table width="100%%" cellpadding="0" cellspacing="0" style="background:#1e293b; border-radius:12px; overflow:hidden;">
                                                <tr style="background:#334155;">
                                                    <th style="padding:12px 16px; text-align:left; color:#e2e8f0; font-size:13px;">Name</th>
                                                    <th style="padding:12px 16px; text-align:left; color:#e2e8f0; font-size:13px;">Department</th>
                                                    <th style="padding:12px 16px; text-align:left; color:#e2e8f0; font-size:13px;">Wish</th>
                                                    <th style="padding:12px 16px; text-align:left; color:#e2e8f0; font-size:13px;">Poster</th>
                                                </tr>
                                                %s
                                            </table>
                                        </td>
                                    </tr>
                                    <!-- Footer -->
                                    <tr>
                                        <td style="padding:20px 30px; border-top:1px solid #1e293b; text-align:center;">
                                            <p style="color:#64748b; margin:0; font-size:13px;">
                                                Generated by <strong style="color:#818cf8;">GreetStudio</strong> • All posters are attached to this email.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(
                LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
                posters.size(),
                successCount,
                failedCount,
                posterRows.toString()
        );
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "…";
    }
}

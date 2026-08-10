package com.birthdayposter.service;

import com.birthdayposter.entity.Person;
import com.birthdayposter.entity.Template;
import com.birthdayposter.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ImageProcessingService {

    public byte[] generatePoster(Template template, Person person, String wishText) {
        try {
            // 1. Load Background Template
            BufferedImage bgImage = ImageIO.read(new URL(template.getTemplateUrl()));
            if (bgImage == null) {
                throw new BadRequestException("Could not read template image from URL");
            }

            int width = bgImage.getWidth();
            int height = bgImage.getHeight();

            // Create a new image to draw on
            BufferedImage composite = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = composite.createGraphics();

            // Better rendering quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Draw background
            g2d.drawImage(bgImage, 0, 0, null);

            // 2. Draw Person Photo (if available)
            if (person.getPhotoUrl() != null && !person.getPhotoUrl().isBlank()) {
                try {
                    BufferedImage personPhoto = ImageIO.read(new URL(person.getPhotoUrl()));
                    if (personPhoto != null) {
                        drawCircularPhoto(g2d, personPhoto, width, height);
                    }
                } catch (Exception e) {
                    log.warn("Failed to load person photo for poster generation: {}", e.getMessage());
                }
            }

            // Parse color
            Color textColor = Color.WHITE;
            if (template.getTextColor() != null && template.getTextColor().startsWith("#")) {
                try {
                    textColor = Color.decode(template.getTextColor());
                } catch (NumberFormatException e) {
                    log.warn("Invalid color format: {}", template.getTextColor());
                }
            }
            g2d.setColor(textColor);

            // 3. Draw Name
            Font nameFont = new Font("SansSerif", Font.BOLD, Math.max(width / 15, 30));
            g2d.setFont(nameFont);
            FontMetrics nameMetrics = g2d.getFontMetrics();
            int nameX = (width - nameMetrics.stringWidth(person.getName())) / 2;
            int nameY = (int) (height * 0.65); // Center-bottom
            
            // Add subtle shadow for text readability
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(person.getName(), nameX + 2, nameY + 2);
            g2d.setColor(textColor);
            g2d.drawString(person.getName(), nameX, nameY);

            // 4. Draw Wish Text
            Font wishFont = new Font("SansSerif", Font.PLAIN, Math.max(width / 25, 20));
            g2d.setFont(wishFont);
            int wishY = (int) (height * 0.75);
            drawWrappedText(g2d, wishText, width, wishY, textColor);

            g2d.dispose();

            // 5. Convert to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(composite, "png", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error generating poster image", e);
            throw new BadRequestException("Failed to process images for poster generation.");
        }
    }

    private void drawCircularPhoto(Graphics2D g2d, BufferedImage photo, int canvasWidth, int canvasHeight) {
        int photoSize = Math.max(canvasWidth / 3, 200); // Proportional size
        int x = (canvasWidth - photoSize) / 2;
        int y = (canvasHeight / 2) - (photoSize / 2) - (canvasHeight / 10); // slightly above center

        // Create circular clipping area
        Shape originalClip = g2d.getClip();
        g2d.setClip(new Ellipse2D.Float(x, y, photoSize, photoSize));
        
        // Scale and draw photo
        g2d.drawImage(photo, x, y, photoSize, photoSize, null);
        
        // Restore clip
        g2d.setClip(originalClip);
        
        // Draw a border around the photo
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(Math.max(canvasWidth / 100f, 3f)));
        g2d.drawOval(x, y, photoSize, photoSize);
    }

    private void drawWrappedText(Graphics2D g2d, String text, int canvasWidth, int startY, Color textColor) {
        FontMetrics fm = g2d.getFontMetrics();
        int maxWidth = (int) (canvasWidth * 0.8);
        int xOffset = (canvasWidth - maxWidth) / 2;
        
        String[] words = text.split(" ");
        String currentLine = words[0];
        List<String> lines = new ArrayList<>();

        for (int i = 1; i < words.length; i++) {
            if (fm.stringWidth(currentLine + " " + words[i]) < maxWidth) {
                currentLine += " " + words[i];
            } else {
                lines.add(currentLine);
                currentLine = words[i];
            }
        }
        lines.add(currentLine);

        int y = startY;
        for (String line : lines) {
            int lineX = (canvasWidth - fm.stringWidth(line)) / 2; // Center horizontally
            
            // Text shadow
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(line, lineX + 2, y + 2);
            
            g2d.setColor(textColor);
            g2d.drawString(line, lineX, y);
            y += fm.getHeight() + 5;
        }
    }
}

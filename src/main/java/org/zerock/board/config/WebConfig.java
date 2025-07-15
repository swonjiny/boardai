package org.zerock.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String fileUploadDirectory = "files";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Create upload directory if it doesn't exist
        File uploadDir = new File(fileUploadDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Map the files directory to a URL path for direct access (useful for images in the web editor)
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadDir.getAbsolutePath() + "/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}

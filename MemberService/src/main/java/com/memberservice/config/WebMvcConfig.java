package com.memberservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FilePath filePath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = filePath.getNormal();
        System.out.println(path);
        registry.addResourceHandler("/**")
                .addResourceLocations("file:///" + path);
    }
}

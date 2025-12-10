package com.example.Klein.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình: Khi ai đó gọi http://IP:8080/uploads/...
        // Server sẽ tìm file trong thư mục "uploads" trên máy tính
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
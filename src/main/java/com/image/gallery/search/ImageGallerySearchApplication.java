package com.image.gallery.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImageGallerySearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageGallerySearchApplication.class, args);
    }

}

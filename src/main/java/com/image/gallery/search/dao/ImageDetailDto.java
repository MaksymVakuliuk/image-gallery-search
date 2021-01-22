package com.image.gallery.search.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageDetailDto {
    private String id;
    private String author;
    private String camera;
    private String tags;
    private String croppedImageUrl;
    private String fullImageUrl;
    private String resolution;
}

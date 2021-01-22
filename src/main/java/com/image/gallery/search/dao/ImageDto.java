package com.image.gallery.search.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageDto {
    private String id;
    private String croppedImage;
}

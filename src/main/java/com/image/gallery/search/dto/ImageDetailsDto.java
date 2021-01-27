package com.image.gallery.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageDetailsDto {
    private String id;
    private String author;
    private String camera;
    private String tags;
    @JsonProperty("cropped_picture")
    private String croppedImageUrl;
    @JsonProperty("full_picture")
    private String fullImageUrl;
    private String resolution;
}

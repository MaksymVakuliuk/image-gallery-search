package com.image.gallery.search.dto;

import com.image.gallery.search.model.ImageDetails;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {
    public ImageDto convertToImageDto(ImageDetails imageDetails, String hostName) {
        ImageDto imageDto = new ImageDto();
        imageDto.setId(imageDetails.getId());
        imageDto.setCroppedImage("http://" + hostName + "/pictures/cropped/" + imageDetails.getId());
        return imageDto;
    }

}

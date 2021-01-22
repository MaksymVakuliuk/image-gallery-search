package com.image.gallery.search.dao;

import com.image.gallery.search.models.ImageDetails;
import com.image.gallery.search.models.Tag;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ImageDetailMapper {
    public ImageDetailDto convertToImageDetailDto(ImageDetails imageDetails, String hostName) {
        ImageDetailDto imageDetailDto = new ImageDetailDto();
        imageDetailDto.setId(imageDetails.getId());
        imageDetailDto.setAuthor(imageDetails.getAuthor());
        imageDetailDto.setCamera(imageDetails.getCamera());
        String tags = imageDetails.getTags().stream()
                .map(Tag::getTag)
                .collect(Collectors.joining(" "))
                .strip();
        imageDetailDto.setTags(tags);
        imageDetailDto.setCroppedImageUrl("http://" + hostName
                + "/pictures/cropped/" + imageDetails.getId());
        imageDetailDto.setFullImageUrl("http://" + hostName
                + "/pictures/full-size/" + imageDetails.getId());
        imageDetailDto
                .setResolution(imageDetails.getImageWidth()
                        + "*" + imageDetails.getImageHeight());
        return imageDetailDto;
    }

}

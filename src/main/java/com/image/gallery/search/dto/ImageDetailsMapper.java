package com.image.gallery.search.dto;

import com.image.gallery.search.exseption.GetDataFromUrlException;
import com.image.gallery.search.exseption.MyIoException;
import com.image.gallery.search.model.CroppedImage;
import com.image.gallery.search.model.FullSizeImage;
import com.image.gallery.search.model.ImageDetails;
import com.image.gallery.search.model.Tag;
import com.image.gallery.search.service.CroppedImageService;
import com.image.gallery.search.service.FullSizeImageService;
import com.image.gallery.search.service.TagService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ImageDetailsMapper {
    private final TagService tagService;
    private final FullSizeImageService fullSizeImageService;
    private final CroppedImageService croppedImageService;

    public ImageDetailsDto convertToImageDetailDto(ImageDetails imageDetails, String hostName) {
        ImageDetailsDto imageDetailsDto = new ImageDetailsDto();
        imageDetailsDto.setId(imageDetails.getId());
        imageDetailsDto.setAuthor(imageDetails.getAuthor());
        imageDetailsDto.setCamera(imageDetails.getCamera());
        String tags = imageDetails.getTags().stream()
                .map(Tag::getTag)
                .collect(Collectors.joining(" "))
                .strip();
        imageDetailsDto.setTags(tags);
        imageDetailsDto.setCroppedImageUrl("http://" + hostName
                + "/pictures/cropped/" + imageDetails.getId());
        imageDetailsDto.setFullImageUrl("http://" + hostName
                + "/pictures/full-size/" + imageDetails.getId());
        imageDetailsDto
                .setResolution(imageDetails.getImageWidth()
                        + "*" + imageDetails.getImageHeight());
        return imageDetailsDto;
    }

    public ImageDetails convertFromImageDetailDto(ImageDetailsDto imageDetailsDto) {
        ImageDetails imageDetails = new ImageDetails();
        String id = imageDetailsDto.getId();
        imageDetails.setId(id);
        imageDetails.setAuthor(imageDetailsDto.getAuthor());
        imageDetails.setCamera(imageDetailsDto.getCamera());
        String tags = imageDetailsDto.getTags();
        imageDetails.setTags(getList(tags));
        String croppedPictureUrl = imageDetailsDto.getCroppedImageUrl();
        String fullSizePictureUrl = imageDetailsDto.getFullImageUrl();
        imageDetails.setCroppedImage(getCroppedImage(id, croppedPictureUrl));
        imageDetails.setFullSizeImage(getFullSizeImage(id, fullSizePictureUrl));
        byte[] fullSizeImage = imageDetails.getFullSizeImage().getFullSizeImage();
        int[] imageResolution = getImageResolution(fullSizeImage);
        if (imageResolution != null) {
            imageDetails.setImageWidth(imageResolution[0]);
            imageDetails.setImageHeight(imageResolution[1]);
        }
        return imageDetails;
    }

    private List<Tag> getList(String tags) {
        List<Tag> tagList = Arrays.stream(tags.split(" "))
                .map(t -> {
                    Tag tag = new Tag();
                    tag.setTag(t);
                    return tag;
                })
                .collect(Collectors.toList());
        tagService.saveAll(tagList);
        return tagList;
    }

    private FullSizeImage getFullSizeImage(String id, String fullSizePictureUrl) {
        FullSizeImage fullSizeImage = new FullSizeImage();
        fullSizeImage.setId(id);
        fullSizeImage.setFullSizeImage(getImageByUrl(fullSizePictureUrl));
        return fullSizeImageService.save(fullSizeImage);
    }

    private CroppedImage getCroppedImage(String id, String croppedPictureUrl) {
        CroppedImage croppedImage = new CroppedImage();
        croppedImage.setId(id);
        croppedImage.setCroppedImage(getImageByUrl(croppedPictureUrl));
        return croppedImageService.save(croppedImage);
    }

    private int[] getImageResolution(byte[] image) {
        BufferedImage read = null;
        try {
            read = ImageIO.read(new ByteArrayInputStream(image));
        } catch (IOException e) {
            throw new MyIoException("Failed convert byte[] to BufferedImage.", e);
        }
        return read != null ? new int[]{ read.getWidth(), read.getHeight()} : null;
    }

    private byte[] getImageByUrl(String imageUrl) {
        HttpGet request = null;
        request = new HttpGet(imageUrl.replaceAll(" ", "%20"));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity responseEntity = response.getEntity();
            InputStream content = responseEntity.getContent();
            BufferedImage bufferedImage = ImageIO.read(content);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new GetDataFromUrlException("Failed to get image.", e);
        }
    }
}

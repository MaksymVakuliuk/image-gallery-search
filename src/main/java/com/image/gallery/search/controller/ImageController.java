package com.image.gallery.search.controller;

import com.image.gallery.search.dao.ImageDetailDto;
import com.image.gallery.search.dao.ImageDetailMapper;
import com.image.gallery.search.dao.ImageDto;
import com.image.gallery.search.dao.ImageMapper;
import com.image.gallery.search.model.ImageDetails;
import com.image.gallery.search.service.ImageDetailsService;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ImageController {
    private final ImageDetailsService imageDetailsService;
    private final ImageMapper imageMapper;
    private final ImageDetailMapper imageDetailMapper;

    @GetMapping("/images")
    public Page<ImageDto> getAllImages(@PageableDefault Pageable pageable,
                                       HttpServletRequest httpServletRequest) {
        Page<ImageDetails> allImageDetail = imageDetailsService.findAll(pageable);
        return allImageDetail.map(imageDetails ->
                imageMapper.convertToImageDto(imageDetails, httpServletRequest.getHeader("host")));
    }

    @GetMapping("/images/{id}")
    public ImageDetailDto getImageDetail(@PathVariable String id,
                                         HttpServletRequest httpServletRequest) {
        ImageDetails imageDetails = imageDetailsService.findById(id);
        return imageDetailMapper
                .convertToImageDetailDto(imageDetails, httpServletRequest.getHeader("host"));
    }

    @GetMapping("/search/{searchTerm}")
    public Page<ImageDto> getSearchImages(@PathVariable String searchTerm,
                                          HttpServletRequest httpServletRequest,
                                          Pageable pageable) {
        Page<ImageDetails> allImageDetail = imageDetailsService.search(searchTerm, pageable);
        return allImageDetail.map(imageDetails ->
               imageMapper.convertToImageDto(imageDetails, httpServletRequest.getHeader("host")));
    }

    @GetMapping("pictures/cropped/{id}")
    public HttpEntity<byte[]> getCroppedImage(@PathVariable String id) {
        ImageDetails byId = imageDetailsService.findById(id);
        byte[] croppedImage = byId.getCroppedImage().getCroppedImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(croppedImage.length);
        return new HttpEntity<>(croppedImage, headers);
    }

    @GetMapping("pictures/full-size/{id}")
    public HttpEntity<byte[]> getFullSizeImage(@PathVariable String id) {
        ImageDetails byId = imageDetailsService.findById(id);
        byte[] fullSizeImage = byId.getFullSizeImage().getFullSizeImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(fullSizeImage.length);
        return new HttpEntity<>(fullSizeImage, headers);
    }
}

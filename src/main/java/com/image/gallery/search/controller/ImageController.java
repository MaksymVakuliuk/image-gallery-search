package com.image.gallery.search.controller;

import com.image.gallery.search.dto.ImageDetailsDto;
import com.image.gallery.search.dto.ImageDetailsMapper;
import com.image.gallery.search.dto.ImageDto;
import com.image.gallery.search.dto.ImageMapper;
import com.image.gallery.search.model.ImageDetails;
import com.image.gallery.search.service.ImageDetailsService;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ImageController {
    private final ImageDetailsService imageDetailsService;
    private final ImageMapper imageMapper;
    private final ImageDetailsMapper imageDetailsMapper;

    @GetMapping("/images")
    public ResponseEntity<Object> getAllImages(@PageableDefault Pageable pageable,
                                               HttpServletRequest httpServletRequest) {
        Page<ImageDetails> allImageDetail = imageDetailsService.findAll(pageable);

        Page<ImageDto> imageDtoPage = allImageDetail.map(imageDetails ->
                imageMapper.convertToImageDto(imageDetails, httpServletRequest.getHeader("host")));
        return new ResponseEntity<>(imageDtoPage, HttpStatus.OK);
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Object> getImageDetail(@PathVariable String id,
                                          HttpServletRequest httpServletRequest) {
        ImageDetails imageDetails = imageDetailsService.findById(id);
        ImageDetailsDto imageDetailsDto = imageDetailsMapper
                .convertToImageDetailDto(imageDetails, httpServletRequest.getHeader("host"));
        return new ResponseEntity<>(imageDetailsDto, HttpStatus.OK);
    }

    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<Object> getSearchImages(@PathVariable String searchTerm,
                                          HttpServletRequest httpServletRequest,
                                          Pageable pageable) {
        Page<ImageDetails> allImageDetail = imageDetailsService.search(searchTerm, pageable);
        Page<ImageDto> imageDtoPage = allImageDetail.map(imageDetails ->
                imageMapper.convertToImageDto(imageDetails, httpServletRequest.getHeader("host")));
        return new ResponseEntity<>(imageDtoPage, HttpStatus.OK);
    }

    @GetMapping("pictures/cropped/{id}")
    public ResponseEntity<Object> getCroppedImage(@PathVariable String id) {
        ImageDetails byId = imageDetailsService.findById(id);
        byte[] croppedImage = byId.getCroppedImage().getCroppedImage();
        return createResponseEntityWithImage(croppedImage);
    }

    @GetMapping("pictures/full-size/{id}")
    public ResponseEntity<Object> getFullSizeImage(@PathVariable String id) {
        ImageDetails byId = imageDetailsService.findById(id);
        byte[] croppedImage = byId.getFullSizeImage().getFullSizeImage();
        return createResponseEntityWithImage(croppedImage);
    }

    private ResponseEntity<Object> createResponseEntityWithImage(byte[] image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}

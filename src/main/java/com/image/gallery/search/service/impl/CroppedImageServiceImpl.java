package com.image.gallery.search.service.impl;

import com.image.gallery.search.model.CroppedImage;
import com.image.gallery.search.repository.CroppedImageRepository;
import com.image.gallery.search.service.CroppedImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CroppedImageServiceImpl implements CroppedImageService {
    private final CroppedImageRepository croppedImageRepository;

    @Override
    public CroppedImage save(CroppedImage croppedImage) {
        return croppedImageRepository.save(croppedImage);
    }
}

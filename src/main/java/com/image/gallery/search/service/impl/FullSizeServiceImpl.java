package com.image.gallery.search.service.impl;

import com.image.gallery.search.model.FullSizeImage;
import com.image.gallery.search.repository.FullSizeImageRepository;
import com.image.gallery.search.service.FullSizeImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FullSizeServiceImpl implements FullSizeImageService {
    private final FullSizeImageRepository fullSizeImageRepository;

    @Override
    public FullSizeImage save(FullSizeImage fullSizeImage) {
        return fullSizeImageRepository.save(fullSizeImage);
    }
}

package com.image.gallery.search.service.impl;

import com.image.gallery.search.models.ImageDetails;
import com.image.gallery.search.repository.ImageDetailsRepository;
import com.image.gallery.search.service.ImageDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageDetailServiceImpl implements ImageDetailsService {
    private final ImageDetailsRepository imageDetailsRepository;

    @Override
    public ImageDetails findById(String id) {
        imageDetailsRepository.findById(id);
        return imageDetailsRepository.findById(id).orElseGet(null);
    }

    @Override
    public Page<ImageDetails> findAll(Pageable pageable) {
        return imageDetailsRepository.findAll(pageable);
    }

    @Override
    public ImageDetails save(ImageDetails imageDetails) {
        return imageDetailsRepository.save(imageDetails);
    }

    @Override
    public Page<ImageDetails> search(String searchTerm, Pageable pageable) {
        return imageDetailsRepository.search(searchTerm, pageable);
    }
}

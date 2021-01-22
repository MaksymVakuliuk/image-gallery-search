package com.image.gallery.search.service;

import com.image.gallery.search.models.ImageDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ImageDetailsService {
    ImageDetails findById(String id);

    Page<ImageDetails> findAll(Pageable pageable);

    ImageDetails save(ImageDetails imageDetails);

    Page<ImageDetails> search(String searchTerm, Pageable pageable);
}

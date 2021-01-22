package com.image.gallery.search.repository;

import com.image.gallery.search.models.ImageDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageDetailsRepository extends JpaRepository<ImageDetails, String> {
    @Override
    Page<ImageDetails> findAll(Pageable pageable);

    @Query("SELECT i FROM ImageDetails i JOIN i.tags it WHERE "
            + "i.author LIKE %?1% OR "
            + "i.camera LIKE %?1% OR "
            + "((select count(tag) from it where it.tag like %?1%) > 0)")
    Page<ImageDetails> search(String searchTerm, Pageable pageable);
}

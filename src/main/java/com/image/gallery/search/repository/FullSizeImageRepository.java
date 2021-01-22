package com.image.gallery.search.repository;

import com.image.gallery.search.models.FullSizeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FullSizeImageRepository extends JpaRepository<FullSizeImage, String> {
}

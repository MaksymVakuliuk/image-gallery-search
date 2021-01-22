package com.image.gallery.search.repository;

import com.image.gallery.search.models.CroppedImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CroppedImageRepository extends JpaRepository<CroppedImage, String> {
}

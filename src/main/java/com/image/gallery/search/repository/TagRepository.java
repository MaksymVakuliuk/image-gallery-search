package com.image.gallery.search.repository;

import com.image.gallery.search.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}

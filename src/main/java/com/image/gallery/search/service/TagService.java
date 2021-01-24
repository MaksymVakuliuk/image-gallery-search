package com.image.gallery.search.service;

import com.image.gallery.search.model.Tag;
import java.util.List;

public interface TagService {
    Tag save(Tag tag);

    List<Tag> saveAll(List<Tag> tagList);
}

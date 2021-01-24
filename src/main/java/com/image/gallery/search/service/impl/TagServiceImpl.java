package com.image.gallery.search.service.impl;

import com.image.gallery.search.model.Tag;
import com.image.gallery.search.repository.TagRepository;
import com.image.gallery.search.service.TagService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> saveAll(List<Tag> tagList) {
        return tagRepository.saveAll(tagList);
    }
}

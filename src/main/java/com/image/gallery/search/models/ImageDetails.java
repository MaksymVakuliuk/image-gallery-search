package com.image.gallery.search.models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ImageDetails {
    @Id
    private String id;
    private String author;
    private String camera;
    @OneToMany
    private List<Tag> tags;
    @OneToOne(fetch = FetchType.LAZY)
    private CroppedImage croppedImage;
    @OneToOne(fetch = FetchType.LAZY)
    private FullSizeImage fullSizeImage;
    private int imageHeight;
    private int imageWidth;
}

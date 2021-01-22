package com.image.gallery.search.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class FullSizeImage {
    @Id
    private String id;
    @Lob
    private byte[] fullSizeImage;
}

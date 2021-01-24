package com.image.gallery.search.service.impl;

import com.image.gallery.search.exseption.GetDataFromUrlException;
import com.image.gallery.search.exseption.MyIoException;
import com.image.gallery.search.model.CroppedImage;
import com.image.gallery.search.model.FullSizeImage;
import com.image.gallery.search.model.ImageDetails;
import com.image.gallery.search.model.Tag;
import com.image.gallery.search.service.CachingImageService;
import com.image.gallery.search.service.CroppedImageService;
import com.image.gallery.search.service.FullSizeImageService;
import com.image.gallery.search.service.ImageDetailsService;
import com.image.gallery.search.service.TagService;
import com.image.gallery.search.util.TokenUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class CachingImageServiceImpl implements CachingImageService {
    private static final String IMAGE_URL = "http://interview.agileengine.com/images";
    private final ImageDetailsService imageDetailsService;
    private final FullSizeImageService fullSizeImageService;
    private final CroppedImageService croppedImageService;
    private final TagService tagService;
    private final TokenUtil getTokenUtil;
    private String token;

    public CachingImageServiceImpl(ImageDetailsService imageDetailsService,
                                   FullSizeImageService fullSizeImageService,
                                   CroppedImageService croppedImageService,
                                   TagService tagService,
                                   TokenUtil getTokenUtil) {
        this.imageDetailsService = imageDetailsService;
        this.fullSizeImageService = fullSizeImageService;
        this.croppedImageService = croppedImageService;
        this.tagService = tagService;
        this.getTokenUtil = getTokenUtil;
    }

    @Override
    public void updateCache() {
        HttpGet request = new HttpGet(IMAGE_URL);
        token = getTokenUtil.getToken();
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity httpEntity = response.getEntity();
            String stringEntity = EntityUtils.toString(httpEntity);
            JSONObject pageJson = new JSONObject(stringEntity);
            int pageCount = pageJson.getInt("pageCount");
            for (int i = 1; i < pageCount; i++) {
                getImageIdFromPage(i);
            }
        } catch (IOException e) {
            throw new GetDataFromUrlException("Failed to cache image data.", e);
        }
    }

    private int getImageIdFromPage(int pageNumber) {
        int count = 0;
        HttpGet request = new HttpGet(IMAGE_URL + "?page=" + pageNumber);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity httpEntity = response.getEntity();
            String stringEntity = EntityUtils.toString(httpEntity);
            JSONObject pageJson = new JSONObject(stringEntity);
            JSONArray pictures = pageJson.getJSONArray("pictures");
            int length = pictures.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = pictures.getJSONObject(i);
                count += getImageDetail(jsonObject.getString("id")) != null ? 1 : 0;
            }
            return count;
        } catch (IOException e) {
            throw new GetDataFromUrlException("Failed to get image data from page.", e);
        }
    }

    private ImageDetails getImageDetail(String id) {
        HttpGet request = new HttpGet(IMAGE_URL + "/" + id);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity httpEntity = response.getEntity();
            String stringEntity = EntityUtils.toString(httpEntity);
            JSONObject imageDetailsJson = new JSONObject(stringEntity);
            return getImageDetailFromJson(imageDetailsJson);
        } catch (IOException e) {
            throw new GetDataFromUrlException("Failed to get image details.", e);
        }
    }

    private ImageDetails getImageDetailFromJson(JSONObject imageDetailsJson) {
        ImageDetails imageDetails = new ImageDetails();
        String id = imageDetailsJson.getString("id");
        imageDetails.setId(id);
        imageDetails.setAuthor(imageDetailsJson.getString("author"));
        imageDetails.setCamera(imageDetailsJson.optString("camera", ""));
        String tags = imageDetailsJson.getString("tags");
        imageDetails.setTags(getList(tags));
        String croppedPictureUrl = imageDetailsJson.getString("cropped_picture");
        String fullSizePictureUrl = imageDetailsJson.getString("full_picture");
        imageDetails.setCroppedImage(getCroppedImage(id, croppedPictureUrl));
        imageDetails.setFullSizeImage(getFullSizeImage(id, fullSizePictureUrl));
        byte[] fullSizeImage = imageDetails.getFullSizeImage().getFullSizeImage();
        int[] imageResolution = getImageResolution(fullSizeImage);
        if (imageResolution != null) {
            imageDetails.setImageWidth(imageResolution[0]);
            imageDetails.setImageHeight(imageResolution[1]);
        }
        return imageDetailsService.save(imageDetails);
    }

    private FullSizeImage getFullSizeImage(String id, String fullSizePictureUrl) {
        FullSizeImage fullSizeImage = new FullSizeImage();
        fullSizeImage.setId(id);
        fullSizeImage.setFullSizeImage(getImageByUrl(fullSizePictureUrl));
        return fullSizeImageService.save(fullSizeImage);
    }

    private CroppedImage getCroppedImage(String id, String croppedPictureUrl) {
        CroppedImage croppedImage = new CroppedImage();
        croppedImage.setId(id);
        croppedImage.setCroppedImage(getImageByUrl(croppedPictureUrl));
        return croppedImageService.save(croppedImage);
    }

    private byte[] getImageByUrl(String imageUrl) {
        HttpGet request = null;
        request = new HttpGet(imageUrl.replaceAll(" ", "%20"));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity httpEntity = response.getEntity();
            InputStream content = httpEntity.getContent();
            BufferedImage bufferedImage = ImageIO.read(content);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new GetDataFromUrlException("Failed to get image.", e);
        }
    }

    private int[] getImageResolution(byte[] image) {
        BufferedImage read = null;
        try {
            read = ImageIO.read(new ByteArrayInputStream(image));
        } catch (IOException e) {
            throw new MyIoException("Failed convert byte[] to BufferedImage.", e);
        }
        return read != null ? new int[]{ read.getWidth(), read.getHeight()} : null;
    }

    private List<Tag> getList(String tags) {
        List<Tag> tagList = Arrays.stream(tags.split(" "))
                .map(t -> {
                    Tag tag = new Tag();
                    tag.setTag(t);
                    return tag;
                })
                .collect(Collectors.toList());
        tagService.saveAll(tagList);
        return tagList;
    }
}

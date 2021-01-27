package com.image.gallery.search.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.image.gallery.search.dto.ImageDetailsDto;
import com.image.gallery.search.dto.ImageDetailsMapper;
import com.image.gallery.search.exseption.GetDataFromUrlException;
import com.image.gallery.search.model.ImageDetails;
import com.image.gallery.search.service.CachingImageService;
import com.image.gallery.search.service.CroppedImageService;
import com.image.gallery.search.service.FullSizeImageService;
import com.image.gallery.search.service.ImageDetailsService;
import com.image.gallery.search.service.TagService;
import com.image.gallery.search.util.TokenUtil;
import java.io.IOException;
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
    private final ImageDetailsMapper imageDetailsMapper;
    private final FullSizeImageService fullSizeImageService;
    private final CroppedImageService croppedImageService;
    private final TagService tagService;
    private final TokenUtil tokenUtil;
    private String accessToken;

    public CachingImageServiceImpl(ImageDetailsService imageDetailsService,
                                   ImageDetailsMapper imageDetailsMapper,
                                   FullSizeImageService fullSizeImageService,
                                   CroppedImageService croppedImageService,
                                   TagService tagService,
                                   TokenUtil tokenUtil) {
        this.imageDetailsService = imageDetailsService;
        this.imageDetailsMapper = imageDetailsMapper;
        this.fullSizeImageService = fullSizeImageService;
        this.croppedImageService = croppedImageService;
        this.tagService = tagService;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void updateCache() {
        HttpGet request = new HttpGet(IMAGE_URL);
        accessToken = tokenUtil.getAccessToken();
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            JSONObject pageJson = new JSONObject(responseString);
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
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            JSONObject pageJson = new JSONObject(responseString);
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
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            return getImageDetailFromResponseString(responseString);
        } catch (IOException e) {
            throw new GetDataFromUrlException("Failed to get image details.", e);
        }
    }

    private ImageDetails getImageDetailFromResponseString(String responseString) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ImageDetailsDto imageDetailsDto = null;
        try {
            imageDetailsDto = objectMapper.readValue(responseString, ImageDetailsDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ImageDetails imageDetails = imageDetailsMapper.convertFromImageDetailDto(imageDetailsDto);
        return imageDetailsService.save(imageDetails);
    }
}

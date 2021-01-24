package com.image.gallery.search.util;

import com.image.gallery.search.exseption.GetDataFromUrlException;
import com.image.gallery.search.exseption.InvalidTokenResponseException;
import com.image.gallery.search.exseption.MyEncodingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {
    private static final String IMAGE_URL = "http://interview.agileengine.com/images";

    public String getToken() {
        HttpPost httpPost = new HttpPost("http://interview.agileengine.com/auth");
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity("{\"apiKey\" : \"23567b218376f79d9415\"}");
        } catch (UnsupportedEncodingException e) {
            throw new MyEncodingException("Failed to convert String to StringEntity.", e);
        }
        httpPost.setEntity(postEntity);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "*/*");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String token = getTokenFromEntity(response.getEntity());
            return checkToken(token) ? token : null;
        } catch (IOException e) {
            throw new GetDataFromUrlException("Failed to get token.", e);
        }
    }

    private String getTokenFromEntity(HttpEntity httpEntity) throws IOException {
        String httpEntityString = EntityUtils.toString(httpEntity);
        JSONObject jsonObject = new JSONObject(httpEntityString);
        return jsonObject.getString("token");
    }

    private boolean checkToken(String token) {
        HttpGet request = new HttpGet(IMAGE_URL);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 401) {
                throw new InvalidTokenResponseException("Token is invalid.");
            }
            return true;
        } catch (IOException e) {
            throw new GetDataFromUrlException("Failed to check token.", e);
        }
    }
}

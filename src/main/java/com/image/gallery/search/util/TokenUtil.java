package com.image.gallery.search.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {
    public String getToken() {
        HttpPost httpPost = new HttpPost("http://interview.agileengine.com/auth");
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity("{\"apiKey\" : \"23567b218376f79d9415\"}");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(postEntity);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "*/*");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return getTokenFromEntity(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTokenFromEntity(HttpEntity httpEntity) throws IOException {
        String httpEntityString = EntityUtils.toString(httpEntity);
        JSONObject jsonObject = new JSONObject(httpEntityString);
        return jsonObject.getString("token");
    }
}

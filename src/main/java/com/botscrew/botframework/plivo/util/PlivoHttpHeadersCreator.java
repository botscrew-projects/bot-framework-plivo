package com.botscrew.botframework.plivo.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;

public class PlivoHttpHeadersCreator {

    private PlivoHttpHeadersCreator() {}

    public static HttpHeaders createHeaders(String authId, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        String auth = authId + ":" + authToken;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

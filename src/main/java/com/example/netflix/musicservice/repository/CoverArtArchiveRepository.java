package com.example.netflix.musicservice.repository;

import com.example.netflix.musicservice.modal.CoverArtArchive.AlbumCover;
import com.example.netflix.musicservice.utility.ApplicationConstants;
import jakarta.annotation.PostConstruct;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.Future;

@Repository
public class CoverArtArchiveRepository {

    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;
    private RetryTemplate retryTemplate;

    @Value("${service.coverArtArchive.url}")
    private String url;

    @Autowired
    public CoverArtArchiveRepository(RestTemplateBuilder restTemplateBuilder, RetryTemplate retryTemplate) {
        this.restTemplateBuilder = restTemplateBuilder;
        restTemplate = restTemplateBuilder.build();
        this.retryTemplate = retryTemplate;
    }

    @Async
    @Cacheable(cacheNames = "albumCover")
    public Future<Map.Entry<String, String>> getAlbumCover(String id) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(ApplicationConstants.ACCEPT, ApplicationConstants.APPLICATION_JSON);
        headers.add(ApplicationConstants.HOST, ApplicationConstants.HOST_VALUE);
        try {
            return retryTemplate.execute(retryContext -> {
                ResponseEntity<AlbumCover> albumCoverResponse = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(headers), AlbumCover.class, id);
                Map.Entry<String, String> albumIdAndImageEntry = new AbstractMap.SimpleEntry<>(id, albumCoverResponse.getBody().getImages()[0].getImage());
                return new AsyncResult(albumIdAndImageEntry);
            });
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*@Cacheable(cacheNames = "albumCover")
    public String getAlbumCover(String id) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(ApplicationConstants.ACCEPT, ApplicationConstants.APPLICATION_JSON);
        headers.add(ApplicationConstants.HOST, ApplicationConstants.HOST_VALUE);
        try {
            return retryTemplate.execute(retryContext -> {
                ResponseEntity<AlbumCover> albumCoverResponse = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(headers), AlbumCover.class, id);
                return albumCoverResponse.getBody().getImages()[0].getImage();
            });
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    @PostConstruct
    public void setRedirectionStrategy() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        final CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
    }
}

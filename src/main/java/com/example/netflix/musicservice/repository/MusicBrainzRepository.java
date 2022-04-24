package com.example.netflix.musicservice.repository;

import com.example.netflix.musicservice.modal.musicbrainz.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class MusicBrainzRepository {

    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;

    private RetryTemplate retryTemplate;

    @Value("${service.musicbrainz.url}")
    private String url;

    @Autowired
    public MusicBrainzRepository(RestTemplateBuilder restTemplateBuilder, RetryTemplate retryTemplate) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.retryTemplate = retryTemplate;
        restTemplate = restTemplateBuilder.build();
    }

    @Cacheable(cacheNames = "artistInfo")
    public Artist getArtistInfo(String mbid){
        return retryTemplate.execute(retryContext -> {
            return restTemplate.getForEntity(url, Artist.class, mbid).getBody();
        });
    }
}

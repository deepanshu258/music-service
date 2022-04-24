package com.example.netflix.musicservice.repository;

import com.example.netflix.musicservice.modal.wikipedia.ArtistWikiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
public class WikipediaRepository {

    private RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;
    private RetryTemplate retryTemplate;

    @Value("${service.wikipedia.url}")
    private String url;

    @Autowired
    public WikipediaRepository(RestTemplateBuilder restTemplateBuilder, RetryTemplate retryTemplate) {
        this.restTemplateBuilder = restTemplateBuilder;
        restTemplate = restTemplateBuilder.build();
        this.retryTemplate = retryTemplate;
    }

    @Cacheable(cacheNames = "artistWikiInfo")
    public ArtistWikiInfo getArtistDescription(String artistTitle){

        try {
            return retryTemplate.execute(retryContext -> {
                return restTemplate.getForEntity(url, ArtistWikiInfo.class, artistTitle).getBody();
            });
        }catch (HttpClientErrorException e){
            e.printStackTrace();
            return null;
        }
    }
}

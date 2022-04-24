package com.example.netflix.musicservice.repository;

import com.example.netflix.musicservice.utility.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

@Repository
public class WikidataRepository {

    @Value("${service.wikidata.url}")
    private String url;


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public String readJsonFromUrl(String resourceId) {
        StringBuilder formattedUrl = new StringBuilder(url);
        formattedUrl.append(resourceId).append(ApplicationConstants.JSONEXT);
        String title = null;
        try(InputStream inputStream = new URL(formattedUrl.toString()).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);

            title = json.getJSONObject(ApplicationConstants.ENTITIES).getJSONObject(resourceId).getJSONObject(ApplicationConstants.SITE_LINKS).getJSONObject(ApplicationConstants.ENWIKI).getString(ApplicationConstants.TITLE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return title;
    }

    @Cacheable(cacheNames = "wikidata")
    public String getWikidata(String resourceId){
        return readJsonFromUrl(resourceId);
    }

}

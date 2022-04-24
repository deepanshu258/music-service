package com.example.netflix.musicservice.modal.wikipedia;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ArtistWikiInfo {

    @JsonProperty("extract_html")
    private String description;
}

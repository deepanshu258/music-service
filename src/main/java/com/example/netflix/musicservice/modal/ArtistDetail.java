package com.example.netflix.musicservice.modal;

import lombok.Data;

import java.util.List;

@Data
public class ArtistDetail {

    private String mbid;
    private String name;
    private String gender;
    private String country;
    private String disambiguation;
    private String description;
    private List<Album> albums;
}

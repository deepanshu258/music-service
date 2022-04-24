package com.example.netflix.musicservice.controller;

import com.example.netflix.musicservice.modal.ArtistDetail;
import com.example.netflix.musicservice.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/musify")
public class MusicController {

    private MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService){
        this.musicService = musicService;
    }

    @GetMapping(value = "/music-artist/details/{mbid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArtistDetail getArtistDetails(@PathVariable("mbid") String mbid){
        return musicService.getArtistInfo(mbid);
    }
}

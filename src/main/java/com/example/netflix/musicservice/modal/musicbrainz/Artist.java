package com.example.netflix.musicservice.modal.musicbrainz;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Artist {

    @JsonProperty("release-groups")
    private List<ReleaseGroup> releaseGroups;
    private List<Relation> relations;
    private String name;
    @JsonProperty("short-name")
    private String shortName;
    private String gender;
    private String disambiguation;
    private String country;
}

package com.example.netflix.musicservice.modal.musicbrainz;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReleaseGroup {

    private String title;
    private String id;
    @JsonProperty("first-release-date")
    private String firstReleaseDate;
    @JsonProperty("primary-type-ids")
    private String primaryTypeId;
}

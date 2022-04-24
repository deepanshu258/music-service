package com.example.netflix.musicservice.service;

import com.example.netflix.musicservice.modal.Album;
import com.example.netflix.musicservice.modal.ArtistDetail;
import com.example.netflix.musicservice.modal.musicbrainz.Artist;
import com.example.netflix.musicservice.modal.musicbrainz.Relation;
import com.example.netflix.musicservice.modal.musicbrainz.ReleaseGroup;
import com.example.netflix.musicservice.modal.wikipedia.ArtistWikiInfo;
import com.example.netflix.musicservice.repository.CoverArtArchiveRepository;
import com.example.netflix.musicservice.repository.MusicBrainzRepository;
import com.example.netflix.musicservice.repository.WikidataRepository;
import com.example.netflix.musicservice.repository.WikipediaRepository;
import com.example.netflix.musicservice.utility.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class MusicService {

    private MusicBrainzRepository musicBrainzRepository;
    private WikidataRepository wikidataRepository;
    private WikipediaRepository wikipediaRepository;
    private CoverArtArchiveRepository coverArtArchiveRepository;

    @Autowired
    public MusicService(MusicBrainzRepository musicBrainzRepository, WikidataRepository wikidataRepository,
                        WikipediaRepository wikipediaRepository, CoverArtArchiveRepository coverArtArchiveRepository) {
        this.musicBrainzRepository = musicBrainzRepository;
        this.wikidataRepository = wikidataRepository;
        this.wikipediaRepository = wikipediaRepository;
        this.coverArtArchiveRepository = coverArtArchiveRepository;
    }

    public ArtistDetail getArtistInfo(String mbid) {

        Artist artist = musicBrainzRepository.getArtistInfo(mbid);
        String artistTitle = getArtistTitle(artist);
        String artistDescription = null;
        if(Objects.nonNull(artistTitle))
            artistDescription = getArtistDescription(artistTitle);
        List<Album> albums = getArtistAlbums(artist);
        return mapArtistDetails(artist, mbid, artistDescription, albums);
    }

    public String getArtistTitle(Artist artist) {
        Optional<Relation> wikidataRelation = artist.getRelations().stream().filter(relation -> relation.getType().equalsIgnoreCase(ApplicationConstants.WIKIDATA)).findFirst();
        if (wikidataRelation.isPresent()) {
            String[] resourceUrlSplit = wikidataRelation.get().getUrl().getResource().split(ApplicationConstants.FORWARDSLASH);
            String resourceId = resourceUrlSplit[resourceUrlSplit.length - 1];
            return wikidataRepository.getWikidata(resourceId);
        }
        return null;
    }

    public String getArtistDescription(String artistTitle){
        ArtistWikiInfo artistWikiInfo = null;
        if (!ObjectUtils.isEmpty(artistTitle)) {
            artistWikiInfo = wikipediaRepository.getArtistDescription(artistTitle);
            return Objects.nonNull(artistWikiInfo) ? artistWikiInfo.getDescription() : null;
        }else
            return null;
    }

    public List<Album> getArtistAlbums(Artist artist){
        Map<String, Album> idAlbumMap = new HashMap<>(0);
        List<Future<Map.Entry<String, String>>> futureAlbumImageList = new ArrayList<>(0);
        if (!CollectionUtils.isEmpty(artist.getReleaseGroups())) {
            artist.getReleaseGroups().forEach(releaseGroup -> {
                Future<Map.Entry<String, String>> albumImageFutureEntry = coverArtArchiveRepository.getAlbumCover(releaseGroup.getId());
                futureAlbumImageList.add(albumImageFutureEntry);
                idAlbumMap.put(releaseGroup.getId() ,mapAlbum(releaseGroup));
            });
        }

        while(futureAlbumImageList.size() > 0){
            int i = 0;
            if(Objects.nonNull(futureAlbumImageList.get(i)) && futureAlbumImageList.get(i).isDone()){
                try {
                    if(Objects.nonNull(futureAlbumImageList.get(i).get())) {
                        Album album = idAlbumMap.get(futureAlbumImageList.get(i).get().getKey());
                        if (Objects.nonNull(album))
                            album.setImageUrl(futureAlbumImageList.get(i).get().getValue());
                    }
                    futureAlbumImageList.remove(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return idAlbumMap.values().stream().toList();
    }


    /*public List<Album> getArtistAlbums(Artist artist){
        List<Album> albums = new ArrayList<>(0);
        if (!CollectionUtils.isEmpty(artist.getReleaseGroups())) {
            artist.getReleaseGroups().forEach(releaseGroup -> {
                String imageURL = coverArtArchiveRepository.getAlbumCover(releaseGroup.getId());
                Album album = mapAlbum(releaseGroup);
                album.setImageUrl(imageURL);
                albums.add(album);
            });
        }
        return albums;
    }*/

    public Album mapAlbum(ReleaseGroup releaseGroup){
        Album album = new Album();
        album.setId(releaseGroup.getId());
        album.setTitle(releaseGroup.getTitle());
        //Future<String> futureAlbumImage = coverArtArchiveRepository.getAlbumCover(releaseGroup.getId());
        //futureAlbumImageList.add(futureAlbumImage);
        //album.setImageUrl(imageUrl);
        return album;
    }

    public ArtistDetail mapArtistDetails(Artist artist, String mbid, String description, List<Album> albums){
        ArtistDetail artistDetail = new ArtistDetail();
        artistDetail.setCountry(artist.getCountry());
        artistDetail.setDisambiguation(artist.getDisambiguation());
        artistDetail.setGender(artist.getGender());
        artistDetail.setName(artist.getName());
        artistDetail.setMbid(mbid);
        artistDetail.setDescription(description);
        artistDetail.setAlbums(albums);

        return artistDetail;
    }
}

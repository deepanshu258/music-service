package com.example.netflix.musicservice;

import com.example.netflix.musicservice.controller.MusicController;
import com.example.netflix.musicservice.modal.musicbrainz.Artist;
import com.example.netflix.musicservice.modal.musicbrainz.Relation;
import com.example.netflix.musicservice.modal.musicbrainz.ReleaseGroup;
import com.example.netflix.musicservice.modal.musicbrainz.Url;
import com.example.netflix.musicservice.modal.wikipedia.ArtistWikiInfo;
import com.example.netflix.musicservice.repository.CoverArtArchiveRepository;
import com.example.netflix.musicservice.repository.MusicBrainzRepository;
import com.example.netflix.musicservice.repository.WikidataRepository;
import com.example.netflix.musicservice.repository.WikipediaRepository;
import com.example.netflix.musicservice.service.MusicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class MusicServiceApplicationTests {

	private MockMvc mockMvc;
	@MockBean
	private MusicBrainzRepository musicBrainzRepository;
	@MockBean
	private WikipediaRepository wikipediaRepository;
	@MockBean
	private WikidataRepository wikidataRepository;
	@MockBean
	private CoverArtArchiveRepository coverArtArchiveRepository;

	@BeforeEach
	void setUp() throws IOException, URISyntaxException {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(initController()).build();
	}

	@Test
	public void getArtistInfoTest() throws Exception {

		when(musicBrainzRepository.getArtistInfo(anyString())).thenReturn(mockArtist());
		when(wikidataRepository.getWikidata(anyString())).thenReturn("Q2831");
		when(wikipediaRepository.getArtistDescription(anyString())).thenReturn(mockArtistWikiInfo());

		Map.Entry<String, String> albumIdAndImageEntry = new AbstractMap.SimpleEntry<>("f44f4f73-a714-31a1-a4b8-bfcaaf311f50", "http://coverartarchive.org/release/a89e1d92-5381-4dab-ba51-733137d0e431/15674154080.jpg");

		Future<Map.Entry<String, String>> albumsIdAndImageEntryFuture = new AsyncResult(albumIdAndImageEntry);
		when(coverArtArchiveRepository.getAlbumCover(anyString())).thenReturn(albumsIdAndImageEntryFuture);



		MvcResult result = mockMvc.perform(get("/musify/music-artist/details/65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		String expectedResult = readFromFile("expectedResponse.json");
		JSONAssert.assertEquals("JSON property doesn't Match", expectedResult, result.getResponse().getContentAsString(), true);
	}

	public MusicController initController() {
		MusicService musicService = new MusicService(musicBrainzRepository, wikidataRepository, wikipediaRepository, coverArtArchiveRepository);
		return new MusicController(musicService);
	}

	public Artist mockArtist(){
		Artist artist = new Artist();
		artist.setCountry("US");
		artist.setDisambiguation("");
		artist.setName("Metallica");

		List<Relation> relationList = new ArrayList<>(0);
		Relation relation = new Relation();
		relation.setType("wikidata");

		Url url = new Url();
		url.setId("6c6350fb-ceda-4e00-96e3-995f4633c8d5");
		url.setResource("https://www.wikidata.org/wiki/Q15920");
		relation.setUrl(url);
		artist.setRelations(relationList);

		List<ReleaseGroup> releaseGroups = new ArrayList<>(0);
		ReleaseGroup releaseGroup = new ReleaseGroup();
		releaseGroup.setId("f44f4f73-a714-31a1-a4b8-bfcaaf311f50");
		releaseGroup.setFirstReleaseDate("1983-07-25");
		releaseGroup.setTitle("Kill ’Em All");
		releaseGroup.setPrimaryTypeId("f529b476-6e62-324f-b0aa-1f3e33d313fc");

		return artist;
	}

	public ArtistWikiInfo mockArtistWikiInfo(){
		ArtistWikiInfo artistWikiInfo = new ArtistWikiInfo();
		artistWikiInfo.setDescription("Metallica is an American heavy metal band. The band was formed in 1981 in Los Angeles by vocalist/guitarist James Hetfield and drummer Lars Ulrich, and has been based in San Francisco for most of its career. The band's fast tempos, instrumentals and aggressive musicianship made them one of the founding \\\"big four\\\" bands of thrash metal, alongside Megadeth, Anthrax and Slayer. Metallica's current lineup comprises founding members and primary songwriters Hetfield and Ulrich, longtime lead guitarist Kirk Hammett and bassist Robert Trujillo. Guitarist Dave Mustaine and bassists Ron McGovney, Cliff Burton and Jason Newsted are former members of the band.\",\n" +
				"\"extract_html\": \"<p><b>Metallica</b> is an American heavy metal band. The band was formed in 1981 in Los Angeles by vocalist/guitarist James Hetfield and drummer Lars Ulrich, and has been based in San Francisco for most of its career. The band's fast tempos, instrumentals and aggressive musicianship made them one of the founding \\\"big four\\\" bands of thrash metal, alongside Megadeth, Anthrax and Slayer. Metallica's current lineup comprises founding members and primary songwriters Hetfield and Ulrich, longtime lead guitarist Kirk Hammett and bassist Robert Trujillo. Guitarist Dave Mustaine and bassists Ron McGovney, Cliff Burton and Jason Newsted are former members of the band.</p>");

		return artistWikiInfo;
	}

	public String readFromFile(String requestPath) {
		if (!StringUtils.isEmpty(requestPath) && getClass().getClassLoader().getResource(requestPath) != null ) {
			Path filePath = null;
			try {
				filePath = Paths.get(getClass().getClassLoader().getResource(requestPath).toURI());
				return Files.readString(filePath);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

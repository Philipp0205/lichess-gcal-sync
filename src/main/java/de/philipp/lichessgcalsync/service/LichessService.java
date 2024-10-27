package de.philipp.lichessgcalsync.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LichessService {

    private final RestTemplate restTemplate;

    public LichessService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getUserGames(String username, int maxGames) {
        String url = UriComponentsBuilder.fromHttpUrl("https://lichess.org/api/games/user/" + username)
                .queryParam("max", maxGames)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}

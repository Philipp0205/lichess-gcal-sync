package de.philipp.lichessgcalsync.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final String TOKEN_ENDPOINT = "https://lichess.org/api/token";
    private static final String LICHESS_URI = "https://lichess.org";

    public CompletableFuture<String> exchangeAuthorizationCodeForAccessToken(String code, String codeVerifier, 
                                                                             String redirectUri, String clientId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String tokenParamsString = "grant_type=authorization_code" + "&code="
                    + URLEncoder.encode(code, StandardCharsets.UTF_8) + "&redirect_uri="
                    + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) + "&client_id="
                    + URLEncoder.encode(clientId, StandardCharsets.UTF_8) + "&code_verifier="
                    + URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8);

            HttpRequest tokenRequest = HttpRequest.newBuilder(URI.create(TOKEN_ENDPOINT))
                    .POST(HttpRequest.BodyPublishers.ofString(tokenParamsString))
                    .header("Content-Type", "application/x-www-form-urlencoded").build();

            HttpClient.newHttpClient().sendAsync(tokenRequest, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            String accessToken = parseField("access_token", response.body());
                            if (accessToken != null) {
                                future.complete(accessToken);
                            } else {
                                future.completeExceptionally(
                                        new Exception("Failed to parse access token from response"));
                            }
                        } else {
                            future.completeExceptionally(new Exception("Token endpoint returned status code: "
                                    + response.statusCode() + ", body: " + response.body()));
                        }
                    }).exceptionally(ex -> {
                        future.completeExceptionally(ex);
                        return null;
                    });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public String readEmail(String accessToken) throws Exception {
        HttpRequest emailRequest = HttpRequest.newBuilder(URI.create(LICHESS_URI + "/api/account/email")).GET()
                .header("authorization", "Bearer " + accessToken).header("accept", "application/json").build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(emailRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseField("email", response.body());
        } else {
            throw new Exception("Failed to retrieve email, status code: " + response.statusCode());
        }
    }
    
    public String readAccountInfo(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(LICHESS_URI + "/api/account")).GET()
                .header("authorization", "Bearer " + accessToken).header("accept", "application/json").build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("Failed to retrieve account info, status code: " + response.statusCode() + ", body: " + response.body());
        }
    }
    
    public String readTimeline(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(LICHESS_URI + "/api/timeline")).GET()
                .header("authorization", "Bearer " + accessToken)
                .header("accept", "application/json")
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("Failed to retrieve timeline, status code: " + response.statusCode() + ", body: " + response.body());
        }
    }
    
    public String readGames(String username, String accessToken, int maxGames) throws Exception {
        String url = LICHESS_URI + "/api/games/user/" + username + "?max=" + maxGames;
        
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
//                .header("authorization", "Bearer " + accessToken)
                .header("accept", "application/x-ndjson") // Lichess games API returns NDJSON
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body(); // Returns NDJSON with each game on a new line
        } else {
            throw new Exception("Failed to retrieve games, status code: " + response.statusCode() + ", body: " + response.body());
        }
    }

    

    private String parseField(String fieldName, String responseBody) {
        String[] tokens = responseBody.split("\"" + fieldName + "\":\"");
        if (tokens.length > 1) {
            return tokens[1].split("\"")[0];
        }
        return null;
    }
    
    
}


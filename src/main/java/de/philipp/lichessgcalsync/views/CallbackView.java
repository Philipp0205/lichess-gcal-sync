package de.philipp.lichessgcalsync.views;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("callback")
@PageTitle("Lichess Callback")
@AnonymousAllowed
public class CallbackView extends VerticalLayout implements BeforeEnterObserver {

	private static final String TOKEN_ENDPOINT = "https://lichess.org/api/token";
	private static final String LICHESS_URI = "https://lichess.org";

	private String accessToken = null;

	public record StateAndCharacter(String code, String state) {
	}

	public CallbackView() {
	}

	private void constructUI() {
		System.out.println("constructUI");
		Text text = new Text("Callback");
		Text emailText = new Text("");
		VerticalLayout layout = new VerticalLayout();

		if (accessToken != null) {
			String email = "";
			H1 h1 = new H1("User is logged in!!");
			try {
				email = readEmail(accessToken);
				System.out.println("Email: " + email);
				emailText = new Text("Email: " + email);
				layout.add(h1, emailText);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		add(layout);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		String token = returnAccessTokenIfPresent();
		if (token != null) {
			System.out.println("Access token already present: " + token);
			this.accessToken = token;
			constructUI();
			return;
		}

		// Extract code and state parameters from the map
		StateAndCharacter stateAndCharacter = extractStateAndCharacter(event);

		if (stateAndCharacter.code == null || stateAndCharacter.state == null) {
			add(new Text("Authorization Failed: Missing code or state."));
			return;
		}

		String redirectUri = (String) VaadinSession.getCurrent().getAttribute("redirectUri");
		String clientId = (String) VaadinSession.getCurrent().getAttribute("clientId");
		String codeVerifier = (String) VaadinSession.getCurrent().getAttribute("codeVerifier");
		System.out.println("redirectUri: " + redirectUri + " clientId: " + clientId + " codeVerifier: " + codeVerifier);

		// Exchange authorization code for access token
		exchangeAuthorizationCodeForAccessToken(stateAndCharacter.code, codeVerifier, redirectUri, clientId)
				.thenAccept(accessToken -> {
					getUI().ifPresent(ui -> ui.access(() -> {
						System.out.println("Authorization Successful: " + accessToken);
						try {
							String email = readEmail(accessToken);
							VaadinSession.getCurrent().setAttribute("access_token", accessToken);
							System.out.println("Email: " + email);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}));
				}).exceptionally(ex -> {
					getUI().ifPresent(ui -> ui.access(() -> {
						System.out.println("Authorization Failed: " + ex.getMessage());
					}));
					return null;
				});
	}

	private StateAndCharacter extractStateAndCharacter(BeforeEnterEvent event) {
		Map<String, List<String>> parametersMap = event.getLocation().getQueryParameters().getParameters();
		String code = parametersMap.getOrDefault("code", List.of()).stream().findFirst().orElse(null);
		String state = parametersMap.getOrDefault("state", List.of()).stream().findFirst().orElse(null);
		return new StateAndCharacter(code, state);
	}

	private String returnAccessTokenIfPresent() {
		return (String) VaadinSession.getCurrent().getAttribute("access_token");
	}

	private CompletableFuture<String> exchangeAuthorizationCodeForAccessToken(String code, String codeVerifier,
			String redirectUri, String clientId) {
		CompletableFuture<String> future = new CompletableFuture<>();

		try {
			// Prepare the token request parameters as URL-encoded string
			String tokenParamsString = "grant_type=authorization_code" + "&code="
					+ URLEncoder.encode(code, StandardCharsets.UTF_8) + "&redirect_uri="
					+ URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) + "&client_id="
					+ URLEncoder.encode(clientId, StandardCharsets.UTF_8) + "&code_verifier="
					+ URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8);

			// Create the POST request to the token endpoint
			HttpRequest tokenRequest = HttpRequest.newBuilder(URI.create(TOKEN_ENDPOINT))
					.POST(HttpRequest.BodyPublishers.ofString(tokenParamsString))
					.header("Content-Type", "application/x-www-form-urlencoded").build();

			// Send the request asynchronously
			HttpClient.newHttpClient().sendAsync(tokenRequest, HttpResponse.BodyHandlers.ofString())
					.thenAccept(response -> {
						if (response.statusCode() == 200) {
							System.out.println(response.body());
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

	private String parseField(String fieldName, String responseBody) {
		// Very simple parsing logic, assuming the response is in JSON format.
		String[] tokens = responseBody.split("\"" + fieldName + "\":\"");
		if (tokens.length > 1) {
			return tokens[1].split("\"")[0];
		}
		return null;
	}

	private String readEmail(String accessToken) throws Exception {

		// Get that e-mail
		HttpRequest emailRequest = HttpRequest.newBuilder(URI.create(LICHESS_URI + "/api/account/email")).GET()
				.header("authorization", "Bearer " + accessToken).header("accept", "application/json").build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(emailRequest, BodyHandlers.ofString());
		int statusCode = response.statusCode();
		String body = response.body();
		String email = parseField("email", body);
		if (statusCode != 200) {
			System.out.println("/api/account/email - " + statusCode);
		}
		return email;

	}
}
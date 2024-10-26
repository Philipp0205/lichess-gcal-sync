package de.philipp.lichessgcalsync.views;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("lichess")
@AnonymousAllowed
public class LichessPKCEView extends VerticalLayout {

	public static String lichessUri = "https://lichess.org";

	public LichessPKCEView() {
		Button loginButton = new Button("Login with Lichess", event -> {
			login();
		});
		add(loginButton);
	}

	private void login() {
		String code_verifier = generateRandomCodeVerifier();

		String code_challenge_method = "S256";
		String code_challenge = generateCodeChallenge(code_verifier);
		String response_type = "code";
		String client_id = "apptest";
		String redirect_uri = "http://" + "localhost" + ":" + 8080 + "/callback";
		String scope = "email:read";
		String state = generateRandomState();
		
		VaadinSession.getCurrent().setAttribute("clientId", client_id);
		VaadinSession.getCurrent().setAttribute("redirectUri", redirect_uri);
		VaadinSession.getCurrent().setAttribute("codeVerifier", code_verifier);
		
		Map<String,String> parameters = Map.of(
				"code_challenge_method", code_challenge_method,
                "code_challenge", code_challenge,
                "response_type", response_type,
                "client_id", client_id,
                "redirect_uri", redirect_uri,
                "scope", scope,
                "state", state
                );
		
		String paramString = parameters.entrySet().stream()
            .map(kv -> kv.getKey() + "=" + kv.getValue())
            .collect(Collectors.joining("&"));
		
        URI frontChannelUrl = URI.create(lichessUri + "/oauth" + "?" + paramString);
        
        UI.getCurrent().getPage().setLocation(frontChannelUrl.toString());
	}

	static String generateRandomCodeVerifier() {
		byte[] bytes = new byte[32];
		new Random().nextBytes(bytes);
		String code_verifier = encodeToString(bytes);
		return code_verifier;
	}

	static String generateCodeChallenge(String code_verifier) {
		byte[] asciiBytes = code_verifier.getBytes(StandardCharsets.US_ASCII);
		MessageDigest md;

		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException nsa_ehhh) {
			throw new RuntimeException(nsa_ehhh);
		}

		byte[] s256bytes = md.digest(asciiBytes);

		String code_challenge = encodeToString(s256bytes);
		return code_challenge;
	}

	static String generateRandomState() {
		byte[] bytes = new byte[16];
		new Random().nextBytes(bytes);
		// Not sure how long the parameter "should" be,
		// going for 8 characters here...
		return encodeToString(bytes).substring(0, 8);
	}

	static String encodeToString(byte[] bytes) {
		return Base64.getUrlEncoder().encodeToString(bytes).replaceAll("=", "").replaceAll("\\+", "-").replaceAll("\\/",
				"_");
	}
}

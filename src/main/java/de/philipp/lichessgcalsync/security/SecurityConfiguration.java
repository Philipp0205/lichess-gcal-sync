package de.philipp.lichessgcalsync.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
	
	   private static final String LOGIN_URL = "/login";

	   @Override
	   protected void configure(HttpSecurity http) throws Exception {
	       super.configure(http);
	       http.oauth2Login().loginPage(LOGIN_URL).permitAll();
	   }
}
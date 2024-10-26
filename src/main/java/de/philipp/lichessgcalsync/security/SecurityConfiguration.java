package de.philipp.lichessgcalsync.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import de.philipp.lichessgcalsync.views.LichessPKCEView;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
	
	   private static final String LOGIN_URL = "/lichess";

	   @Override
	   protected void configure(HttpSecurity http) throws Exception {
	        http.oauth2Login().loginPage(LOGIN_URL).permitAll();
	        super.configure(http);

	       // Register your login view to the navigation access control mechanism:
	       setLoginView(http, LichessPKCEView.class);
	   }

	@Override
	public void configure(WebSecurity web) throws Exception {
		// Customize your WebSecurity configuration.
		super.configure(web);
	}

	/**
	 * Demo UserDetailsManager which only provides two hardcoded in memory users and
	 * their roles. NOTE: This shouldn't be used in real world applications.
	 */
	@Bean
	public UserDetailsManager userDetailsService() {
		UserDetails user = User.withUsername("user").password("{noop}user").roles("USER").build();
		UserDetails admin = User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
		return new InMemoryUserDetailsManager(user, admin);
	}
}
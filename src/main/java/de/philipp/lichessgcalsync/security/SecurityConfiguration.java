package de.philipp.lichessgcalsync.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import de.philipp.lichessgcalsync.views.LoginView;

@EnableWebSecurity 
@Configuration
public class SecurityConfiguration
                extends VaadinWebSecurity { 



	  @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        // Delegating the responsibility of general configurations
	        // of http security to the super class. It's configuring
	        // the followings: Vaadin's CSRF protection by ignoring
	        // framework's internal requests, default request cache,
	        // ignoring public views annotated with @AnonymousAllowed,
	        // restricting access to other views/endpoints, and enabling
	        // NavigationAccessControl authorization.
	        // You can add any possible extra configurations of your own
	        // here (the following is just an example):

	        // http.rememberMe().alwaysRemember(false);

	        // Configure your static resources with public access before calling
	        // super.configure(HttpSecurity) as it adds final anyRequest matcher
	        http.authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/public/**"))
	            .permitAll());
	        
	        http.formLogin(form -> form
	                .loginPage("/login") // The login view
	                .defaultSuccessUrl("/", true) // Redirect to "/home" after successful login
	                .failureUrl("/login?error=true") // Redirect back to login on failure
	            );

	        super.configure(http); 

	        // This is important to register your login view to the
	        // navigation access control mechanism:
	        setLoginView(http, LoginView.class); 
	    }
	  
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
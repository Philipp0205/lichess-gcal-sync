package de.philipp.lichessgcalsync.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LichessUserDetailService implements UserDetailsService{
    private static final Logger logger = LoggerFactory.getLogger(LichessUserDetailService.class);
	private final LichessUserRepository userRepository; 
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public LichessUserDetailService(LichessUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 logger.info("Attempting login for user: {}", username);
		LichessUser user = userRepository.findByEmail(username);
		if (user == null) {
            logger.error("User not found: {}", username);
			throw new UsernameNotFoundException("User not found");
		}
		return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
	}
	
	public boolean saveUser(LichessUser user) {
		if (user == null) {
			return false;
		}
		
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		System.out.println("service: " + user.getEmail());

		LichessUser existingUser = userRepository.findByEmail(user.getEmail());
		if (existingUser != null) {
			System.out.println("user already exists");
			return false;
		}

		userRepository.save(user);
		System.out.println("user saved");
		return true;
	}

}

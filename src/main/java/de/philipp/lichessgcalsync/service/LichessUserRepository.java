package de.philipp.lichessgcalsync.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LichessUserRepository extends JpaRepository<LichessUser, Long> {

	@Query("SELECT u FROM LichessUser u WHERE lower(u.email) = lower(:email)")
	LichessUser findByEmail(@Param("email") String email);

}

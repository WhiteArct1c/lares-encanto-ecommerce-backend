package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByEmail(String email);

}

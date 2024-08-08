package com.laresencanto.laresencantorestapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laresencanto.laresencantorestapi.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}

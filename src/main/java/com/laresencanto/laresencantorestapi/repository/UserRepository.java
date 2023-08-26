package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

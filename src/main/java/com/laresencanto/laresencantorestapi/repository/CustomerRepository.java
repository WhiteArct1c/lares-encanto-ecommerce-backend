package com.laresencanto.laresencantorestapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.User;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByFullName(String fullName);
    Optional<Customer> findByCpf(String cpf);
    Optional<Customer> findByBirthDate(String birthDate);
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByUser (User user);

    Optional<Customer> findByUserId(Long id);
}

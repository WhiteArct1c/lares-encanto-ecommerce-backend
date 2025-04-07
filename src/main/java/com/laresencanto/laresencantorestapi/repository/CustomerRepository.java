package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.user.User;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByFullName(String fullName);
    Optional<Customer> findByCpf(String cpf);
    Optional<Customer> findByBirthDate(String birthDate);
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByUser (User user);

    @EntityGraph(attributePaths = {"address", "address.categories", "creditCardList", "gender"})
    Optional<Customer> findByUserId(Long id);

    boolean existsByCpf(@CPF(message = "CPF deve ser válido") String cpf);
}

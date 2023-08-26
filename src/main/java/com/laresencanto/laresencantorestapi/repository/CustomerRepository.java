package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}

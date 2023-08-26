package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

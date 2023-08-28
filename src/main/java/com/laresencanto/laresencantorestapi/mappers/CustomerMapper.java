package com.laresencanto.laresencantorestapi.mappers;

import com.laresencanto.laresencantorestapi.domain.Address;
import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.Gender;
import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.utils.enums.UserRole;

import java.util.HashSet;
import java.util.Set;

public class CustomerMapper {

    public static Customer saveCustomerRequestMapper(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        Gender gender = new Gender();
        User user = new User();
        Address addressCustomer = new Address();
        Set<Address> addresses = new HashSet<>();

        gender.setName(customerRequestDTO.gender().toString());

        customer.setFullName(customerRequestDTO.fullName());
        customer.setCpf(customerRequestDTO.cpf());
        customer.setBirthDate(customerRequestDTO.birthDate());
        customer.setPhone(customerRequestDTO.phone());
        customer.setGender(gender);

        user.setEmail(customerRequestDTO.user().email());
        user.setPassword(customerRequestDTO.user().password());
        user.setRole(UserRole.USER); //sempre será usuários comuns

        addressCustomer.setTitle(customerRequestDTO.address().title());
        addressCustomer.setCep(customerRequestDTO.address().cep());
        addressCustomer.setResidenceType(customerRequestDTO.address().residenceType());
        addressCustomer.setAddressType(customerRequestDTO.address().addressType());
        addressCustomer.setStreetName(customerRequestDTO.address().streetName());
        addressCustomer.setAddressNumber(customerRequestDTO.address().addressNumber());
        addressCustomer.setNeighborhoods(customerRequestDTO.address().neighborhoods());
        addressCustomer.setCity(customerRequestDTO.address().city());
        addressCustomer.setState(customerRequestDTO.address().state());
        addressCustomer.setCountry(customerRequestDTO.address().country());
        addressCustomer.setObservations(customerRequestDTO.address().observations());

        addresses.add(addressCustomer);

        customer.setUser(user);
        customer.setAddress(addresses);

        return customer;
    }
}

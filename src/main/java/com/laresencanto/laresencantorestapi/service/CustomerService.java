package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.Address;
import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.Gender;
import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerUpdateResponseDTO;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.GenderRepository;
import com.laresencanto.laresencantorestapi.utils.enums.UserRole;
import com.laresencanto.laresencantorestapi.validation.UserValidation;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final GenderRepository genderRepository;
    private final UserValidation userValidation = new UserValidation();

    public CustomerService(CustomerRepository customerRepository, GenderRepository genderRepository){
        this.customerRepository = customerRepository;
        this.genderRepository = genderRepository;
    }

    public ResponseDTO<CustomerResponseDTO> saveCustomer(CustomerRequestDTO customerRequestDTO){
        String errors = validateCustomerRequestData(customerRequestDTO.user());

        if(!errors.isEmpty()){
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), errors, null);
        }

        Customer customer = saveCustomerRequestMapper(customerRequestDTO);
        customer.getUser().setPassword(new BCryptPasswordEncoder().encode(customer.getUser().getPassword()));

        Customer newCustomer = customerRepository.save(customer);

        CustomerResponseDTO response = new CustomerResponseDTO(
                newCustomer.getId(),
                newCustomer.getFullName(),
                newCustomer.getCpf(),
                newCustomer.getBirthDate(),
                newCustomer.getPhone(),
                newCustomer.getGender(),
                newCustomer.getAddress()
        );

        return new ResponseDTO<>(HttpStatus.CREATED.toString(), "Cliente salvo com sucesso", List.of(response));
    }

    public ResponseDTO<CustomerResponseDTO> listAllCostumers(){
        List<Customer> customers = customerRepository.findAll();
        List<CustomerResponseDTO> response = new ArrayList<>();

        for(Customer customer: customers) {
            response.add(
                    new CustomerResponseDTO(
                            customer.getId(),
                            customer.getFullName(),
                            customer.getCpf(),
                            customer.getBirthDate(),
                            customer.getPhone(),
                            customer.getGender(),
                            customer.getAddress()
                    )
            );
        }
        return new ResponseDTO<>(HttpStatus.OK.toString(), "Lista de clientes resgatada com sucesso", response);
    }

    public ResponseDTO<CustomerUpdateResponseDTO> update(CustomerUpdateRequestDTO customerUpdateRequestDTO){
        List<CustomerUpdateResponseDTO> response = new ArrayList<>();
        Optional<Customer> optionalCustomer = customerRepository.findByCpf(customerUpdateRequestDTO.cpf());
        Gender gender = genderRepository.findByName(customerUpdateRequestDTO.gender().name());

        if(optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();

            customer.setFullName(customerUpdateRequestDTO.fullName());
            customer.setCpf(customerUpdateRequestDTO.cpf());
            customer.setBirthDate(customerUpdateRequestDTO.birthDate());
            customer.setPhone(customerUpdateRequestDTO.phone());
            customer.setGender(gender);

            Customer updatedCustomer = customerRepository.save(customer);

            response.add(
                    new CustomerUpdateResponseDTO(
                            updatedCustomer.getId(),
                            updatedCustomer.getFullName(),
                            updatedCustomer.getCpf(),
                            updatedCustomer.getBirthDate(),
                            updatedCustomer.getPhone(),
                            updatedCustomer.getGender()
                    )
            );

            return new ResponseDTO<>(HttpStatus.OK.toString(), "Dados atualizados com sucesso", response);
        }else{
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Erro ao atualizar os dados, por favor, tente novamente mais tarde", null);
        }
    }

    private Customer saveCustomerRequestMapper(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        Gender gender = genderRepository.findByName(customerRequestDTO.gender().name());
        User user = new User();
        Address addressCustomer = new Address();
        Set<Address> addresses = new HashSet<>();

        gender.setName(customerRequestDTO.gender().toString());

        customer.setFullName(customerRequestDTO.fullName());
        customer.setCpf(customerRequestDTO.cpf());
        customer.setBirthDate(customerRequestDTO.birthDate());
        customer.setPhone(customerRequestDTO.phone());
        customer.setGender(gender);
        customer.setRanking("0"); //default ranking

        user.setEmail(customerRequestDTO.user().email());
        user.setPassword(customerRequestDTO.user().password());
        user.setRole(UserRole.USER); //sempre será usuários comuns
        user.setIsActive("1");

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

    private String validateCustomerRequestData(RegisterRequestDTO registerRequestDTO){
        StringBuilder errors = new StringBuilder();

        errors.append(userValidation.validateCustomerRequestRules(registerRequestDTO));

        return errors.toString();
    }

}

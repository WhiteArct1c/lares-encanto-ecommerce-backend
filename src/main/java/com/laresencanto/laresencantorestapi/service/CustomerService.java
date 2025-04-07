package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.address.Address;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.customer.Gender;
import com.laresencanto.laresencantorestapi.domain.user.User;
import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.address.AddressResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseAdminDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerUpdateResponseDTO;
import com.laresencanto.laresencantorestapi.exception.CustomerNotFoundException;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.GenderRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import com.laresencanto.laresencantorestapi.utils.enums.UserRole;
import com.laresencanto.laresencantorestapi.validation.UserValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final GenderRepository genderRepository;
    private final UserValidation userValidation = new UserValidation();
    private final UserRepository userRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            GenderRepository genderRepository,

            UserRepository userRepository){
        this.customerRepository = customerRepository;
        this.genderRepository = genderRepository;
        this.userRepository = userRepository;
    }

    public ResponseDTO<CustomerResponseDTO> saveCustomer(CustomerRequestDTO customerRequestDTO){
        String errors = validateCustomerRequestData(customerRequestDTO.user());

        if(!errors.isEmpty()){
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), errors, null);
        }

        if(customerRepository.existsByCpf(customerRequestDTO.cpf())){
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Já existe um cliente cadastrado com esse CPF", null);
        }

        if(userRepository.existsByEmail(customerRequestDTO.user().email())){
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Já existe um usuário cadastrado com esse e-mail", null);
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
                newCustomer.getAddress().stream().map(ad -> new AddressResponseDTO(
                        ad.getId(),
                        ad.getTitle(),
                        ad.getCep(),
                        ad.getResidenceType(),
                        ad.getAddressType(),
                        ad.getCategories().stream().map(AddressCategory::toString).collect(Collectors.toSet()),
                        ad.getStreetName(),
                        ad.getAddressNumber(),
                        ad.getNeighborhoods(),
                        ad.getCity(),
                        ad.getState(),
                        ad.getCountry(),
                        ad.getObservations()
                )).collect(Collectors.toSet()),
                null
        );

        return new ResponseDTO<>(HttpStatus.CREATED.toString(), "Cliente salvo com sucesso", List.of(response));
    }

    public Page<CustomerResponseAdminDTO> listAllCustomers(Pageable pageable) throws CustomerNotFoundException {
        try {
            return customerRepository.findAll(pageable).map(customer -> new CustomerResponseAdminDTO(
                    customer.getId(),
                    customer.getFullName(),
                    customer.getCpf(),
                    customer.getBirthDate(),
                    customer.getPhone(),
                    customer.getGender(),
                    customer.getRanking(),
                    customer.getUser().getRole().name(),
                    customer.getUser().getIsActive(),
                    customer.getAddress(),
                    null
            ));
        } catch (Exception e) {
            throw new CustomerNotFoundException("Nenhum cliente encontrado");
        }
    }

    public ResponseDTO<CustomerUpdateResponseDTO> update(CustomerUpdateRequestDTO customerUpdateRequestDTO){
        CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findById(customerAuth.id()).orElseThrow();
        List<CustomerUpdateResponseDTO> response = new ArrayList<>();
        Gender gender = genderRepository.findByName(customerUpdateRequestDTO.gender().name());


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
        addressCustomer.setCategories(customerRequestDTO.address().addressCategories().stream().map(AddressCategory::fromString).collect(Collectors.toSet()));
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
        return userValidation.validateCustomerRequestRules(registerRequestDTO);
    }


}

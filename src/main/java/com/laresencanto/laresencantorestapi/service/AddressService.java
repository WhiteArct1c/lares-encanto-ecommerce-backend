package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.Address;
import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.error.ResponseErrorDTO;
import com.laresencanto.laresencantorestapi.repository.AddressRepository;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final TokenService tokenService;

    public AddressService(
            AddressRepository addressRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            TokenService tokenService
    ){
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.tokenService = tokenService;
    }

    public ResponseDTO save(AddressUpdateRequestDTO address){
        var decodedToken = tokenService.decodedJwtToken(address.token());
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Optional<Customer> customer = customerRepository.findByUser(user);

        if(customer.isPresent()) {
            Set<Address> addresses = customer.get().getAddress();
            Address newAddress = new Address(
                    address.address().title(),
                    address.address().cep(),
                    address.address().residenceType(),
                    address.address().addressType(),
                    address.address().streetName(),
                    address.address().addressNumber(),
                    address.address().neighborhoods(),
                    address.address().state(),
                    address.address().city(),
                    address.address().country(),
                    address.address().observations()
            );

            Address savedAddress = addressRepository.save(newAddress);

            addresses.add(savedAddress);

            customer.get().setAddress(addresses);

            customerRepository.save(customer.get());

            return new ResponseDTO(HttpStatus.OK.toString(), "Endereço salvo com sucesso!", null);
        }

        return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Erro ao salvar endereço!", null);
    }

    public ResponseDTO delete(String id){

        Optional<Address> address = addressRepository.findById(Long.parseLong(id));

        if(address.isPresent()){
            addressRepository.delete(address.get());
            return new ResponseDTO<>(HttpStatus.OK.toString(), "Endereço excluído com sucesso!", null);
        }else{
            return  new ResponseDTO<ResponseErrorDTO>(HttpStatus.BAD_REQUEST.toString(), "Endereço informado não encontrado!", null);
        }
    }
}

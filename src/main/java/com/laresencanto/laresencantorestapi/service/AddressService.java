package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.address.Address;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.user.User;
import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressAddRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.address.AddressResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.error.ResponseErrorDTO;
import com.laresencanto.laresencantorestapi.repository.AddressRepository;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public ResponseDTO save(AddressAddRequestDTO address){
        var decodedToken = tokenService.decodedJwtToken(address.token());
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Optional<Customer> customer = customerRepository.findByUser(user);

        if(customer.isPresent()) {
            Set<Address> addresses = customer.get().getAddress();

            Address billingAddress = addresses.stream()
                    .filter(a -> a.getCategories().contains(AddressCategory.BILLING))
                    .findFirst()
                    .orElse(null);

            if(address.address().addressCategories().isEmpty()){
                return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "O tipo do endereço é obrigatório!", null);
            }

            if(billingAddress != null && address.address().addressCategories().contains(AddressCategory.BILLING.getCategory().toUpperCase())){
                addresses.stream()
                        .filter(a -> a.getCategories().contains(AddressCategory.BILLING))
                        .findFirst()
                        .ifPresent(a -> {
                            a.getCategories().remove(AddressCategory.BILLING);
                        });
            }

            if(addresses.isEmpty() && !address.address().addressCategories().contains(AddressCategory.BILLING.getCategory().toUpperCase())){
                return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "O primeiro endereço cadastrado deve ser de cobrança!", null);
            }

            Address newAddress = new Address(
                    address.address().title(),
                    address.address().cep(),
                    address.address().residenceType(),
                    address.address().addressType(),
                    address.address().addressCategories().stream().map(AddressCategory::fromString).collect(Collectors.toSet()),
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
        CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findById(customerAuth.id()).orElseThrow();

        Set<Address> customerAddresses = customer.getAddress();
        Optional<Address> address = addressRepository.findById(Long.parseLong(id));

        if(address.isPresent()){
            // Soft delete: marca como inativo em vez de remover fisicamente
            Address addr = address.get();

            if(addr.getCategories().contains(AddressCategory.BILLING) && customerAddresses.size() > 1){
                customerAddresses.stream()
                        .filter(a -> !a.getCategories().contains(AddressCategory.BILLING))
                        .findFirst()
                        .ifPresent(a -> {
                            a.getCategories().add(AddressCategory.BILLING);
                            addressRepository.save(a);
                        });
            }

            addr.setIsActive(false);
            addressRepository.save(addr);
            return new ResponseDTO<>(HttpStatus.OK.toString(), "Endereço excluído com sucesso!", null);
        }else{
            return  new ResponseDTO<ResponseErrorDTO>(HttpStatus.BAD_REQUEST.toString(), "Endereço informado não encontrado!", null);
        }
    }

    public ResponseDTO update(AddressUpdateRequestDTO address) {
        var decodedToken = tokenService.decodedJwtToken(address.token());
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Optional<Customer> customer = customerRepository.findByUser(user);
        Address addressToUpdate = addressRepository.findById(Long.parseLong(address.address().id())).orElseThrow();

        if(customer.isEmpty()) {
            return new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Erro ao atualizar endereço!", null);
        }

        Set<Address> addresses = customer.get().getAddress();
        Address billingAddress = addresses.stream()
                .filter(a -> a.getCategories().contains(AddressCategory.BILLING))
                .findFirst()
                .orElse(null);

        //checking if the address is the billing address
        if(billingAddress != null){
            //if was the same address
            if(billingAddress.getId() == Long.parseLong(address.address().id())){
                if(!address.address().addressCategories().contains(AddressCategory.BILLING.getCategory().toUpperCase()) && addresses.size() == 1){
                    return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Este é o único endereço cadastrado e de cobrança, deve ter pelo menos um endereço de cobrança!", null);
                }

                //if the address is not more the billing address and there are more than one address
                if(!address.address().addressCategories().contains(AddressCategory.BILLING.getCategory().toUpperCase()) && addresses.size() > 1){
                    //switching the billing address
                    addresses.stream()
                            .filter(a -> !a.getCategories().contains(AddressCategory.BILLING))
                            .findFirst()
                            .ifPresent(a -> {
                                a.getCategories().add(AddressCategory.BILLING);
                                addressRepository.save(a);
                            });
                }
            }

        }

        //switching the billing address if the new address is billing
        if(billingAddress != null && address.address().addressCategories().contains(AddressCategory.BILLING.getCategory().toUpperCase())){
            addresses.stream()
                    .filter(a -> a.getCategories().contains(AddressCategory.BILLING))
                    .findFirst()
                    .ifPresent(a -> {
                        a.getCategories().remove(AddressCategory.BILLING);
                        addressRepository.save(a);
                    });
        }

        addressToUpdate.setTitle(address.address().title());
        addressToUpdate.setCep(address.address().cep());
        addressToUpdate.setResidenceType(address.address().residenceType());
        addressToUpdate.setAddressType(address.address().addressType());
        addressToUpdate.setCategories(address.address().addressCategories().stream().map(AddressCategory::fromString).collect(Collectors.toSet()));
        addressToUpdate.setStreetName(address.address().streetName());
        addressToUpdate.setAddressNumber(address.address().addressNumber());
        addressToUpdate.setNeighborhoods(address.address().neighborhoods());
        addressToUpdate.setState(address.address().state());
        addressToUpdate.setCity(address.address().city());
        addressToUpdate.setCountry(address.address().country());
        addressToUpdate.setObservations(address.address().observations());
        addressToUpdate.setCustomer(customer.get());

        Address savedAddress = addressRepository.save(addressToUpdate);

        AddressResponseDTO response = new AddressResponseDTO(
                savedAddress.getId(),
                savedAddress.getTitle(),
                savedAddress.getCep(),
                savedAddress.getResidenceType(),
                savedAddress.getAddressType(),
                savedAddress.getCategories().stream().map(AddressCategory::getCategory).collect(Collectors.toSet()),
                savedAddress.getStreetName(),
                savedAddress.getAddressNumber(),
                savedAddress.getNeighborhoods(),
                savedAddress.getState(),
                savedAddress.getCity(),
                savedAddress.getCountry(),
                savedAddress.getObservations()
        );

        return new ResponseDTO<>(HttpStatus.OK.toString(), "Endereço atualizado com sucesso!", List.of(response));
    }
}

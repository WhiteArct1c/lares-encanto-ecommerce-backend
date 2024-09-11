package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.user.UpdatePasswordRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import com.laresencanto.laresencantorestapi.validation.UserValidation;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final UserValidation userValidation = new UserValidation();

    public UserService(
            UserRepository userRepository,
            TokenService tokenService
    ){
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public ResponseDTO updatePassword(UpdatePasswordRequestDTO updatePasswordRequestDTO){

        try{
            var decodedToken = tokenService.decodedJwtToken(updatePasswordRequestDTO.token());
            User user = (User) userRepository.findByEmail(decodedToken.getSubject());

            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(user.getEmail(), updatePasswordRequestDTO.password(), updatePasswordRequestDTO.confirmedPassword());

            String errors = validateCustomerRequestData(registerRequestDTO);

            if(errors.isEmpty()){
                user.setPassword(new BCryptPasswordEncoder().encode(updatePasswordRequestDTO.password()));
                userRepository.save(user);

                return new ResponseDTO(HttpStatus.OK.toString(), "Senha atualizada com sucesso!", null);
            }

            return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), errors, null);

        }catch (Exception e){
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Erro ao atualizar senha!", null);
        }
    }

    @Transactional
    public ResponseDTO<String> activateUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            user.get().setIsActive("1");
            try{
                userRepository.save(user.get());
            }catch (Exception e){
                return new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Erro ao ativar usuário!", null);
            }
            return new ResponseDTO<>(HttpStatus.OK.toString(), "Usuário ativado com sucesso!", null);
        }
        return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Usuário não encontrado, verifique o id informado!", null);
    }

    @Transactional
    public ResponseDTO<String> deactivateUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            user.get().setIsActive("0");
            try{
                userRepository.save(user.get());
            }catch (Exception e){
                return  new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Erro ao desativar usuário!", null);
            }
            return new ResponseDTO<>(HttpStatus.OK.toString(), "Usuário desativado com sucesso!", null);
        }
        return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Usuário não encontrado, verifique o id informado!", null);
    }

    private String validateCustomerRequestData(RegisterRequestDTO registerRequestDTO){
        StringBuilder errors = new StringBuilder();

        errors.append(userValidation.validateCustomerRequestRules(registerRequestDTO));

        return errors.toString();
    }
}

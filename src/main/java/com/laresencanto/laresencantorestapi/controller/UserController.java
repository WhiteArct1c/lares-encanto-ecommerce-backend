package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.user.UpdatePasswordRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(
        UserService userService
    ){
        this.userService = userService;
    }

    @PostMapping("/update-password")
    public ResponseEntity<ResponseDTO> updatePassword(@RequestBody @Valid UpdatePasswordRequestDTO updatePasswordRequestDTO){
        ResponseDTO response = userService.updatePassword(updatePasswordRequestDTO);
        return ResponseEntity.ok(response);
    }
}

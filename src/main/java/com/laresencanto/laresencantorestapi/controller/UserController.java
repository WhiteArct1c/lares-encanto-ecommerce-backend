package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.user.UpdatePasswordRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/activate/{id}")
    public ResponseEntity<ResponseDTO<String>> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<ResponseDTO<String>> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}

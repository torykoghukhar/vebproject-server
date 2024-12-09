package com.example.vebproject.controllers;

import com.example.vebproject.DTO.ProfileDTO;
import com.example.vebproject.DTO.SignInRequest;
import com.example.vebproject.DTO.SignUpRequest;
import com.example.vebproject.DTO.UserDTO;
import com.example.vebproject.security.JwtUtils;
import com.example.vebproject.services.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public UserController(UserService userService, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInRequest request) {
        System.out.println(request);
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getName(), request.getPasswordHash())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateToken(authentication);
            return ResponseEntity.ok(token);
        }
        catch(BadCredentialsException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registr(@RequestBody SignUpRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (userService.isUsernameAlreadyTaken(request.getName())) {
            errors.put("username", "Користувач з таким логіном існує");
        }
        if (userService.isEmailAlreadyTaken(request.getEmail())) {
            errors.put("email", "Користувач з такою поштою існує");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        if (userService.register(request)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/profiledata")
    public ResponseEntity<?> getProfile(Principal principal){
        if (principal ==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    @PutMapping("/updateprofile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileDTO profileDTO) {
        if (profileDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(userService.updateProfile(profileDTO)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/deleteprofile")
    public ResponseEntity<?> deleteProfile(Principal principal) {
        if (principal ==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (userService.deleteProfile(principal.getName())){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/users")
    public ResponseEntity<?> users(){
        List<UserDTO> userDTOS= userService.getAllUsers();
        if (userDTOS.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userDTOS);
    }

    @PostMapping("/ban/{Id}")
    public ResponseEntity<?> banUser(@PathVariable Long Id){
        if (Id == null){
            return ResponseEntity.badRequest().build();
        }
        boolean result = userService.banUser(Id);
        if(result){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/users/upload-photo/{name}")
    public ResponseEntity<String> uploadProfilePhoto(
            @PathVariable String name,
            @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = userService.uploadProfilePhoto(name, file);
            return ResponseEntity.ok(photoUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload photo");
        }
    }

    @GetMapping("/uploads/profile-photos/{filename}")
    public ResponseEntity<org.springframework.core.io.Resource> getProfilePhoto(@PathVariable String filename) {
        try {
            // Путь к файлу в папке, где храните фото
            Path filePath = Paths.get("/uploads/profile-photos").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG) // Укажите нужный MIME-тип, например IMAGE_PNG, IMAGE_JPEG и т.д.
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
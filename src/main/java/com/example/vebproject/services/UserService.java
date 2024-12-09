package com.example.vebproject.services;

import com.example.vebproject.DTO.ProfileDTO;
import com.example.vebproject.DTO.SignUpRequest;
import com.example.vebproject.DTO.UserDTO;
import com.example.vebproject.models.Role;
import com.example.vebproject.models.User;
import com.example.vebproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isUsernameAlreadyTaken(String name) {
        return userRepository.existsByName(name);
    }

    public boolean isEmailAlreadyTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean register(SignUpRequest request) {
        User user = new User(
                request.getName(), request.getEmail(), request.getFirstName(), request.getLastName(), request.getPhone(), request.getAddress(), passwordEncoder.encode(request.getPasswordHash()), Role.ROLE_USER);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public User getuser(String username) {
        return userRepository.findByName(username).orElse(null);
    }

    public ProfileDTO getProfile(String username) {
        return userRepository.findByName(username).stream()
                .map(user -> new ProfileDTO(
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getAddress(),
                        user.getProfilePictureUrl()
                ))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public boolean updateProfile(ProfileDTO profileDTO) {
        User user = getuser(profileDTO.getName());
        if (user != null) {
            if (isInfoTakenByOtherUser(profileDTO.getName(), profileDTO.getEmail(), user)){
                return false;
            }
            user.setName(profileDTO.getName());
            user.setEmail(profileDTO.getEmail());
            user.setPhone(profileDTO.getPhone());
            user.setAddress(profileDTO.getAddress());
            user.setProfilePictureUrl(profileDTO.getProfilePictureUrl());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private boolean isInfoTakenByOtherUser(String username, String email, User currentUser) {
        User userWithSameUsername = userRepository.findByName(username).orElse(null);
        User userWithSameEmail = userRepository.findByEmail(email).orElse(null);

        return (userWithSameUsername != null && !userWithSameUsername.getId().equals(currentUser.getId())) ||
                (userWithSameEmail != null && !userWithSameEmail.getId().equals(currentUser.getId()));
    }

    public boolean deleteProfile(String username) {
        User user = getuser(username);
        if (user != null) {
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    public String uploadProfilePhoto(String name, MultipartFile file) throws IOException {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Сохранение файла на диск (или в облако)
        String fileName = name + "_" + file.getOriginalFilename();
        String uploadDir = "/uploads/profile-photos/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Обновление URL в базе
        String photoUrl = "/uploads/profile-photos/" + fileName;
        user.setProfilePictureUrl(photoUrl);
        userRepository.save(user);

        return photoUrl;
    }


    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !Role.ROLE_ADMIN.equals(user.getRole()) && !user.isBan())
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public boolean banUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null || user.isBan()) {
            return false;
        }
        user.setBan(true);
        userRepository.save(user);
        return true;
    }
}


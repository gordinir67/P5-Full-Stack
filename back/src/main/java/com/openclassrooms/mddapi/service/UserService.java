package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.user.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.user.UserDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service métier de gestion du profil utilisateur.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construit le service utilisateur.
     *
     * @param userRepository dépôt des utilisateurs
     * @param userMapper mapper des utilisateurs
     * @param passwordEncoder encodeur de mots de passe
     */
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retourne le profil de l'utilisateur connecté.
     *
     * @param userId identifiant de l'utilisateur
     * @return données du profil
     */
    public UserDto getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        return userMapper.toDto(user);
    }

    /**
     * Met à jour le profil de l'utilisateur connecté.
     *
     * @param userId identifiant de l'utilisateur
     * @param request données de mise à jour
     * @return profil mis à jour
     */
    public UserDto updateMe(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (request.getEmail() != null) {
            String newEmail = request.getEmail().trim().toLowerCase();

            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
            }

            user.setEmail(newEmail);
        }

        if (request.getName() != null) {
            user.setName(request.getName().trim());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toDto(userRepository.save(user));
    }
}

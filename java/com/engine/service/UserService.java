package com.engine.service;

import com.engine.model.Usuario;
import com.engine.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarUsuario(String username, String rawPassword) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("El nombre de usuario ya existe.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        // CRUCIAL: Guardar la contraseña encriptada, nunca en texto plano
        nuevoUsuario.setPassword(passwordEncoder.encode(rawPassword));

        userRepository.save(nuevoUsuario);
    }
}
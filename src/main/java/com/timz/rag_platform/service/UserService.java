package com.timz.rag_platform.service;

import com.timz.rag_platform.model.User;
import com.timz.rag_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User creerUser(String email, String password, 
                          String nom, String prenom, User.Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setRole(role);
        user.setActif(true);
        return userRepository.save(user);
    }

    public List<User> getTousLesUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserParEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public long compterUsers() {
        return userRepository.count();
    }

    public void supprimerUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean emailExiste(String email) {
        return userRepository.existsByEmail(email);
    }
}
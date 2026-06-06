package com.timz.rag_platform.controller;

import com.timz.rag_platform.model.Document;
import com.timz.rag_platform.model.User;
import com.timz.rag_platform.repository.UserRepository;
import com.timz.rag_platform.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/documents")
    public String documents(Model model, Authentication auth) {
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        
        if (userOpt.isEmpty()) {
            // Créer l'utilisateur dans la BD s'il n'existe pas
            User newUser = new User();
            newUser.setEmail(auth.getName());
            newUser.setPassword("N/A");
            newUser.setNom("Admin");
            newUser.setPrenom("System");
            newUser.setRole(User.Role.ADMIN);
            newUser.setActif(true);
            userRepository.save(newUser);
            model.addAttribute("documents", List.of());
            model.addAttribute("nombreDocuments", 0);
            return "documents";
        }

        User user = userOpt.get();
        List<Document> documents = documentService.getDocumentsParUser(user);
        model.addAttribute("documents", documents);
        model.addAttribute("nombreDocuments", documents.size());
        return "documents";
    }

    @PostMapping("/documents/upload")
    public String uploadDocument(@RequestParam("fichier") MultipartFile fichier,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(auth.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Utilisateur non trouve.");
                return "redirect:/documents";
            }
            documentService.sauvegarderDocument(fichier, userOpt.get());
            redirectAttributes.addFlashAttribute("success", "Document uploade avec succes !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'upload : " + e.getMessage());
        }
        return "redirect:/documents";
    }

    @PostMapping("/documents/supprimer/{id}")
    public String supprimerDocument(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            documentService.supprimerDocument(id);
            redirectAttributes.addFlashAttribute("success", "Document supprime avec succes !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression.");
        }
        return "redirect:/documents";
    }
}
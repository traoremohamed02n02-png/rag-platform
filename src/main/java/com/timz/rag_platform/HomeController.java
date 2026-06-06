package com.timz.rag_platform;

import com.timz.rag_platform.repository.DocumentRepository;
import com.timz.rag_platform.repository.QuestionRepository;
import com.timz.rag_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("nombreDocuments", documentRepository.count());
        model.addAttribute("nombreUsers", userRepository.count());
        model.addAttribute("nombreQuestions", questionRepository.count());
        return "dashboard";
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @GetMapping("/history")
    public String history() {
        return "history";
    }
}
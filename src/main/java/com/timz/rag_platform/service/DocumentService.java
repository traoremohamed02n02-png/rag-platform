package com.timz.rag_platform.service;

import com.timz.rag_platform.model.Document;
import com.timz.rag_platform.model.User;
import com.timz.rag_platform.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    private final String UPLOAD_DIR = "uploads/";

    public Document sauvegarderDocument(MultipartFile fichier, User user) throws IOException {
        // Créer le dossier uploads s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Sauvegarder le fichier sur le disque
        String nomFichier = System.currentTimeMillis() + "_" + fichier.getOriginalFilename();
        Path cheminFichier = uploadPath.resolve(nomFichier);
        Files.copy(fichier.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

        // Déterminer le type
        String type = determinerType(fichier.getOriginalFilename());

        // Créer l'entité Document
        Document document = new Document();
        document.setNom(fichier.getOriginalFilename());
        document.setType(type);
        document.setTaille(fichier.getSize());
        document.setCheminFichier(cheminFichier.toString());
        document.setUser(user);
        document.setStatut(Document.Statut.INDEXE);
        document.setCreatedAt(java.time.LocalDateTime.now());

        return documentRepository.save(document);
    }

    public List<Document> getDocumentsParUser(User user) {
        return documentRepository.findByUser(user);
    }

    public List<Document> getTousLesDocuments() {
        return documentRepository.findAll();
    }

    public long compterDocuments() {
        return documentRepository.count();
    }

    public void supprimerDocument(Long id) {
        documentRepository.deleteById(id);
    }

    private String determinerType(String nomFichier) {
        if (nomFichier == null) return "INCONNU";
        String nom = nomFichier.toLowerCase();
        if (nom.endsWith(".pdf")) return "PDF";
        if (nom.endsWith(".xlsx") || nom.endsWith(".xls")) return "EXCEL";
        if (nom.endsWith(".png") || nom.endsWith(".jpg") || nom.endsWith(".jpeg")) return "IMAGE";
        return "AUTRE";
    }
}
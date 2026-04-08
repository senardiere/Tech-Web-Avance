package com.clinique.endpoint;

import com.clinique.dao.UtilisateurDAO;
import com.clinique.entity.Admin;
import com.clinique.enums.Role;
import com.clinique.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/internal/admins")
public class AdminEndpoint {

    @Autowired
    private UtilisateurDAO utilisateurDAO;

    @Autowired
    private AuditService auditService;

    @PostMapping
    public Map<String, Object> createAdmin(@RequestBody Map<String, String> data) {
        // Vérifier si le login existe déjà
        if (utilisateurDAO.existsByLogin(data.get("login"))) {
            throw new RuntimeException("Un utilisateur avec ce login existe déjà");
        }

        // Vérifier si l'email existe déjà
        if (utilisateurDAO.existsByEmail(data.get("email"))) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        Admin admin = new Admin();
        admin.setNom(data.get("nom"));
        admin.setPrenom(data.get("prenom"));
        admin.setEmail(data.get("email"));
        admin.setLogin(data.get("login"));
        admin.setMotDePasse(data.get("motDePasse"));
        admin.setTelephone(data.get("telephone"));
        admin.setDepartement(data.get("departement"));
        admin.setRole(Role.ADMIN);
        admin.setActif(true);
        admin.setDateCreation(LocalDateTime.now());

        Admin saved = (Admin) utilisateurDAO.save(admin);

        auditService.log("CREATE", "Admin", saved.getId(),
                "Création administrateur: " + saved.getLogin());

        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("login", saved.getLogin());
        response.put("nom", saved.getNom());
        response.put("prenom", saved.getPrenom());
        response.put("email", saved.getEmail());
        response.put("role", saved.getRole().name());

        return response;
    }
}
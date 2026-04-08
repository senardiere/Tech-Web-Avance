package com.clinique.endpoint;

import com.clinique.entity.Admin;
import com.clinique.entity.Medecin;
import com.clinique.entity.Utilisateur;
import com.clinique.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/internal/auth")
public class AuthEndpoint {

    @Autowired
    private AuthService authService;

    // ========== AUTHENTIFICATION ==========

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String login = credentials.get("login");
        String motDePasse = credentials.get("motDePasse");

        Utilisateur user = authService.login(login, motDePasse);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("login", user.getLogin());
        response.put("nom", user.getNom());
        response.put("prenom", user.getPrenom());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("actif", user.isActif());
        response.put("type", user instanceof Admin ? "ADMIN" : "MEDECIN");

        return response;
    }

    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }

    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser() {
        Utilisateur user = authService.getCurrentUser();

        if (user == null) {
            return Map.of("authenticated", false);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("id", user.getId());
        response.put("login", user.getLogin());
        response.put("nom", user.getNom());
        response.put("prenom", user.getPrenom());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("actif", user.isActif());
        response.put("type", user instanceof Admin ? "ADMIN" : "MEDECIN");

        return response;
    }

    @GetMapping("/is-authenticated")
    public Map<String, Boolean> isAuthenticated() {
        return Map.of("authenticated", authService.isAuthenticated());
    }

    @GetMapping("/is-admin")
    public Map<String, Boolean> isAdmin() {
        return Map.of("isAdmin", authService.isAdmin());
    }

    @GetMapping("/is-medecin")
    public Map<String, Boolean> isMedecin() {
        return Map.of("isMedecin", authService.isMedecin());
    }

    // ========== CRÉATION D'UTILISATEURS ==========

    @PostMapping("/admins")
    public Map<String, Object> createAdmin(@RequestBody Map<String, String> data) {
        Admin admin = new Admin();
        admin.setNom(data.get("nom"));
        admin.setPrenom(data.get("prenom"));
        admin.setEmail(data.get("email"));
        admin.setLogin(data.get("login"));
        admin.setMotDePasse(data.get("motDePasse"));
        admin.setTelephone(data.get("telephone"));
        admin.setDepartement(data.get("departement"));

        Admin saved = authService.createAdmin(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("login", saved.getLogin());
        response.put("nom", saved.getNom());
        response.put("prenom", saved.getPrenom());
        response.put("email", saved.getEmail());
        response.put("role", saved.getRole().name());

        return response;
    }

    @PostMapping("/medecins")
    public Map<String, Object> createMedecin(@RequestBody Map<String, Object> data) {
        Medecin medecin = new Medecin();
        medecin.setNom((String) data.get("nom"));
        medecin.setPrenom((String) data.get("prenom"));
        medecin.setEmail((String) data.get("email"));
        medecin.setLogin((String) data.get("login"));
        medecin.setMotDePasse((String) data.get("motDePasse"));
        medecin.setTelephone((String) data.get("telephone"));
        medecin.setNumeroLicence((String) data.get("numeroLicence"));
        medecin.setCabinet((String) data.get("cabinet"));

        if (data.get("specialiteId") != null) {
            // medecin.setSpecialite(...); // À ajouter si nécessaire
        }

        Medecin saved = authService.createMedecin(medecin);

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
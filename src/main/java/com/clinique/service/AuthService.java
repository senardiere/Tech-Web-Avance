package com.clinique.service;

import com.clinique.dao.UtilisateurDAO;
import com.clinique.entity.Admin;
import com.clinique.entity.Medecin;
import com.clinique.entity.Utilisateur;
import com.clinique.enums.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UtilisateurDAO utilisateurDAO;

    @Autowired
    private AuditService auditService;

    // ========== MÉTHODES EXISTANTES ==========

    @Transactional
    public Utilisateur login(String login, String motDePasse) {
        Utilisateur utilisateur = utilisateurDAO.findByLogin(login);

        if (utilisateur == null) {
            throw new RuntimeException("Login incorrect");
        }

        if (!utilisateur.isActif()) {
            throw new RuntimeException("Compte désactivé");
        }

        if (!motDePasse.equals(utilisateur.getMotDePasse())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        utilisateur.setDerniereConnexion(LocalDateTime.now());
        utilisateurDAO.save(utilisateur);

        HttpSession session = getSession();
        session.setAttribute("userId", utilisateur.getId());

        auditService.log("LOGIN", utilisateur.getClass().getSimpleName(),
                utilisateur.getId(), "Connexion");

        return utilisateur;
    }

    public void logout() {
        HttpSession session = getSession();
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user != null) {
                auditService.log("LOGOUT", user.getClass().getSimpleName(),
                        userId, "Déconnexion");
            }
        }

        session.invalidate();
    }

    public Utilisateur getCurrentUser() {
        HttpSession session = getSession();
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            return utilisateurDAO.findById(userId);
        }
        return null;
    }

    public boolean isAuthenticated() {
        return getSession().getAttribute("userId") != null;
    }

    public boolean isAdmin() {
        Utilisateur user = getCurrentUser();
        return user != null && "ADMIN".equals(user.getRole().name());
    }

    public boolean isMedecin() {
        Utilisateur user = getCurrentUser();
        return user != null && "MEDECIN".equals(user.getRole().name());
    }

    private HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    // ========== NOUVELLES MÉTHODES POUR LA CRÉATION ==========

    @Transactional
    public Admin createAdmin(Admin admin) {
        // Vérifier si le login existe déjà
        if (utilisateurDAO.existsByLogin(admin.getLogin())) {
            throw new RuntimeException("Un utilisateur avec ce login existe déjà");
        }

        // Vérifier si l'email existe déjà
        if (utilisateurDAO.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        admin.setRole(Role.ADMIN);
        admin.setActif(true);
        admin.setDateCreation(LocalDateTime.now());

        Admin saved = (Admin) utilisateurDAO.save(admin);

        auditService.log("CREATE", "Admin", saved.getId(),
                "Création administrateur: " + saved.getLogin());

        return saved;
    }

    @Transactional
    public Medecin createMedecin(Medecin medecin) {
        // Vérifier si le login existe déjà
        if (utilisateurDAO.existsByLogin(medecin.getLogin())) {
            throw new RuntimeException("Un utilisateur avec ce login existe déjà");
        }

        // Vérifier si l'email existe déjà
        if (utilisateurDAO.existsByEmail(medecin.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        medecin.setRole(Role.MEDECIN);
        medecin.setActif(true);
        medecin.setDateCreation(LocalDateTime.now());

        Medecin saved = (Medecin) utilisateurDAO.save(medecin);

        auditService.log("CREATE", "Medecin", saved.getId(),
                "Création médecin: " + saved.getLogin());

        return saved;
    }
}
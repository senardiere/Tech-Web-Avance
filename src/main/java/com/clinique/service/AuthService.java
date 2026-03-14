package com.clinique.service;

import com.clinique.dao.UtilisateurDAO;
import com.clinique.entity.Utilisateur;
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
        // ✅ Stocker seulement l'ID en session (pas l'objet complet)
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
            return utilisateurDAO.findById(userId);  // Retourne Admin ou Medecin
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
}
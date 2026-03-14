package com.clinique.service;

import com.clinique.dao.AuditLogDAO;
import com.clinique.entity.AuditLog;
import com.clinique.entity.Utilisateur;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditService {

    @Autowired
    private AuditLogDAO auditLogDAO;

    @Autowired
    private AuthService authService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String entite, Long entiteId, String details) {
        AuditLog log = new AuditLog();

        Utilisateur user = authService.getCurrentUser();
        log.setUtilisateur(user != null ? user.getLogin() : "ANONYME");
        log.setAction(action);
        log.setEntite(entite);
        log.setEntiteId(entiteId);
        log.setDetails(details);

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            log.setAdresseIp(request.getRemoteAddr());
        } catch (Exception e) {
            log.setAdresseIp("INCONNUE");
        }

        auditLogDAO.save(log);
    }
}
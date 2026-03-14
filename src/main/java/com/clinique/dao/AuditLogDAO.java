package com.clinique.dao;

import com.clinique.entity.AuditLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class AuditLogDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sauvegarder un log
     */
    public AuditLog save(AuditLog log) {
        if (log.getId() == null) {
            entityManager.persist(log);
            return log;
        } else {
            return entityManager.merge(log);
        }
    }

    /**
     * Trouver un log par son ID
     */
    public AuditLog findById(Long id) {
        return entityManager.find(AuditLog.class, id);
    }

    /**
     * Trouver tous les logs (avec limite)
     */
    public List<AuditLog> findAll(int limit) {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a ORDER BY a.dateAction DESC",
                AuditLog.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Trouver tous les logs (sans limite)
     */
    public List<AuditLog> findAll() {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a ORDER BY a.dateAction DESC",
                AuditLog.class);
        return query.getResultList();
    }

    /**
     * Trouver les logs par utilisateur
     */
    public List<AuditLog> findByUtilisateur(String login, int limit) {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a WHERE a.utilisateur = :login ORDER BY a.dateAction DESC",
                AuditLog.class);
        query.setParameter("login", login);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Trouver les logs par action
     */
    public List<AuditLog> findByAction(String action, int limit) {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a WHERE a.action = :action ORDER BY a.dateAction DESC",
                AuditLog.class);
        query.setParameter("action", action);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Trouver les logs par entité
     */
    public List<AuditLog> findByEntite(String entite, int limit) {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a WHERE a.entite = :entite ORDER BY a.dateAction DESC",
                AuditLog.class);
        query.setParameter("entite", entite);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Trouver les logs par entité et ID
     */
    public List<AuditLog> findByEntiteAndId(String entite, Long entiteId, int limit) {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a WHERE a.entite = :entite AND a.entiteId = :entiteId ORDER BY a.dateAction DESC",
                AuditLog.class);
        query.setParameter("entite", entite);
        query.setParameter("entiteId", entiteId);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Trouver les logs par période
     */
    public List<AuditLog> findByPeriode(LocalDateTime debut, LocalDateTime fin, int limit) {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a WHERE a.dateAction BETWEEN :debut AND :fin ORDER BY a.dateAction DESC",
                AuditLog.class);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Compter le nombre de logs
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(a) FROM AuditLog a", Long.class);
        return query.getSingleResult();
    }

    /**
     * Supprimer les logs anciens (plus de 30 jours)
     */
    public int deleteOldLogs() {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(30);

        return entityManager.createQuery(
                        "DELETE FROM AuditLog a WHERE a.dateAction < :dateLimite")
                .setParameter("dateLimite", dateLimite)
                .executeUpdate();
    }
}
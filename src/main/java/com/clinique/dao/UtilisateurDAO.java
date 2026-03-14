package com.clinique.dao;

import com.clinique.entity.Admin;
import com.clinique.entity.Medecin;
import com.clinique.entity.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class UtilisateurDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sauvegarder un utilisateur (création ou mise à jour)
     */
    public Utilisateur save(Utilisateur utilisateur) {
        if (utilisateur.getId() == null) {
            entityManager.persist(utilisateur);
            return utilisateur;
        } else {
            return entityManager.merge(utilisateur);
        }
    }

    /**
     * Trouver un utilisateur par son ID (retourne Admin ou Medecin automatiquement)
     */
    public Utilisateur findById(Long id) {
        return entityManager.find(Utilisateur.class, id);
    }

    /**
     * Trouver un utilisateur par son ID avec le type spécifique
     */
    public <T extends Utilisateur> T findById(Long id, Class<T> type) {
        return entityManager.find(type, id);
    }

    /**
     * Trouver un utilisateur par son login
     */
    public Utilisateur findByLogin(String login) {
        try {
            TypedQuery<Utilisateur> query = entityManager.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.login = :login",
                    Utilisateur.class);
            query.setParameter("login", login);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouver un utilisateur par son email
     */
    public Utilisateur findByEmail(String email) {
        try {
            TypedQuery<Utilisateur> query = entityManager.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.email = :email",
                    Utilisateur.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouver tous les utilisateurs
     */
    public List<Utilisateur> findAll() {
        TypedQuery<Utilisateur> query = entityManager.createQuery(
                "SELECT u FROM Utilisateur u ORDER BY u.nom, u.prenom",
                Utilisateur.class);
        return query.getResultList();
    }

    /**
     * Trouver tous les utilisateurs par rôle
     */
    public List<? extends Utilisateur> findByRole(String role) {
        if ("ADMIN".equals(role)) {
            return findAdmins();
        } else if ("MEDECIN".equals(role)) {
            return findMedecins();
        } else {
            return findAll();
        }
    }

    /**
     * Trouver tous les administrateurs
     */
    public List<Admin> findAdmins() {
        TypedQuery<Admin> query = entityManager.createQuery(
                "SELECT a FROM Admin a ORDER BY a.nom, a.prenom",
                Admin.class);
        return query.getResultList();
    }

    /**
     * Trouver tous les médecins
     */
    public List<Medecin> findMedecins() {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT m FROM Medecin m ORDER BY m.nom, m.prenom",
                Medecin.class);
        return query.getResultList();
    }

    /**
     * Trouver tous les utilisateurs actifs
     */
    public List<Utilisateur> findAllActifs() {
        TypedQuery<Utilisateur> query = entityManager.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.actif = true ORDER BY u.nom, u.prenom",
                Utilisateur.class);
        return query.getResultList();
    }

    /**
     * Supprimer (désactiver) un utilisateur - soft delete
     */
    public void delete(Long id) {
        Utilisateur utilisateur = findById(id);
        if (utilisateur != null) {
            utilisateur.setActif(false);
            entityManager.merge(utilisateur);
        }
    }

    /**
     * Supprimer définitivement un utilisateur (rare - attention aux contraintes)
     */
    public void hardDelete(Long id) {
        Utilisateur utilisateur = findById(id);
        if (utilisateur != null) {
            entityManager.remove(utilisateur);
        }
    }

    /**
     * Compter le nombre d'utilisateurs
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM Utilisateur u", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compter le nombre d'utilisateurs par rôle
     */
    public long countByRole(String role) {
        if ("ADMIN".equals(role)) {
            return countAdmins();
        } else if ("MEDECIN".equals(role)) {
            return countMedecins();
        } else {
            return count();
        }
    }

    /**
     * Compter le nombre d'administrateurs
     */
    public long countAdmins() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(a) FROM Admin a", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compter le nombre de médecins
     */
    public long countMedecins() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m", Long.class);
        return query.getSingleResult();
    }

    /**
     * Vérifier si un login existe déjà
     */
    public boolean existsByLogin(String login) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM Utilisateur u WHERE u.login = :login", Long.class);
        query.setParameter("login", login);
        return query.getSingleResult() > 0;
    }

    /**
     * Vérifier si un email existe déjà
     */
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM Utilisateur u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    /**
     * Mettre à jour la dernière connexion
     */
    public void updateLastLogin(Long id) {
        Utilisateur utilisateur = findById(id);
        if (utilisateur != null) {
            utilisateur.setDerniereConnexion(LocalDateTime.now());
            entityManager.merge(utilisateur);
        }
    }

    /**
     * Rechercher des utilisateurs par nom, prénom ou email
     */
    public List<Utilisateur> search(String keyword) {
        TypedQuery<Utilisateur> query = entityManager.createQuery(
                "SELECT u FROM Utilisateur u WHERE " +
                        "LOWER(u.nom) LIKE LOWER(:keyword) OR " +
                        "LOWER(u.prenom) LIKE LOWER(:keyword) OR " +
                        "LOWER(u.email) LIKE LOWER(:keyword) " +
                        "ORDER BY u.nom, u.prenom",
                Utilisateur.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    /**
     * Trouver les utilisateurs créés après une certaine date
     */
    public List<Utilisateur> findCreatedAfter(LocalDateTime date) {
        TypedQuery<Utilisateur> query = entityManager.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.dateCreation > :date ORDER BY u.dateCreation DESC",
                Utilisateur.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    /**
     * Trouver les utilisateurs avec dernière connexion avant une certaine date (inactifs)
     */
    public List<Utilisateur> findLastConnectionBefore(LocalDateTime date) {
        TypedQuery<Utilisateur> query = entityManager.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.derniereConnexion < :date OR u.derniereConnexion IS NULL ORDER BY u.derniereConnexion",
                Utilisateur.class);
        query.setParameter("date", date);
        return query.getResultList();
    }
}
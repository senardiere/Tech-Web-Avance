package com.clinique.dao;

import com.clinique.entity.Medecin;
import com.clinique.entity.Specialite;
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
public class MedecinDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sauvegarder un médecin (création ou mise à jour)
     */
    public Medecin save(Medecin medecin) {
        if (medecin.getId() == null) {
            entityManager.persist(medecin);
            return medecin;
        } else {
            return entityManager.merge(medecin);
        }
    }

    /**
     * Trouver un médecin par son ID
     */
    public Medecin findById(Long id) {
        return entityManager.find(Medecin.class, id);
    }

    /**
     * Trouver un médecin par son login
     */
    public Medecin findByLogin(String login) {
        try {
            TypedQuery<Medecin> query = entityManager.createQuery(
                    "SELECT m FROM Medecin m WHERE m.login = :login",
                    Medecin.class);
            query.setParameter("login", login);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouver un médecin par son email
     */
    public Medecin findByEmail(String email) {
        try {
            TypedQuery<Medecin> query = entityManager.createQuery(
                    "SELECT m FROM Medecin m WHERE m.email = :email",
                    Medecin.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouver un médecin par son numéro de licence
     */
    public Medecin findByNumeroLicence(String numeroLicence) {
        try {
            TypedQuery<Medecin> query = entityManager.createQuery(
                    "SELECT m FROM Medecin m WHERE m.numeroLicence = :numeroLicence",
                    Medecin.class);
            query.setParameter("numeroLicence", numeroLicence);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouver tous les médecins
     */
    public List<Medecin> findAll() {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT m FROM Medecin m ORDER BY m.nom, m.prenom",
                Medecin.class);
        return query.getResultList();
    }

    /**
     * Trouver tous les médecins actifs
     */
    public List<Medecin> findAllActifs() {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT m FROM Medecin m WHERE m.actif = true ORDER BY m.nom, m.prenom",
                Medecin.class);
        return query.getResultList();
    }

    /**
     * Trouver les médecins par spécialité
     */
    public List<Medecin> findBySpecialite(Specialite specialite) {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT m FROM Medecin m WHERE m.specialite = :specialite ORDER BY m.nom, m.prenom",
                Medecin.class);
        query.setParameter("specialite", specialite);
        return query.getResultList();
    }

    /**
     * Trouver les médecins par spécialité (par ID)
     */
    public List<Medecin> findBySpecialiteId(Long specialiteId) {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT m FROM Medecin m WHERE m.specialite.id = :specialiteId ORDER BY m.nom, m.prenom",
                Medecin.class);
        query.setParameter("specialiteId", specialiteId);
        return query.getResultList();
    }

    /**
     * Vérifier si un login existe déjà
     */
    public boolean existsByLogin(String login) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m WHERE m.login = :login", Long.class);
        query.setParameter("login", login);
        return query.getSingleResult() > 0;
    }

    /**
     * Vérifier si un email existe déjà
     */
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m WHERE m.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    /**
     * Vérifier si un numéro de licence existe déjà
     */
    public boolean existsByNumeroLicence(String numeroLicence) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m WHERE m.numeroLicence = :numeroLicence", Long.class);
        query.setParameter("numeroLicence", numeroLicence);
        return query.getSingleResult() > 0;
    }

    /**
     * Compter le nombre total de médecins
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compter le nombre de médecins actifs
     */
    public long countActifs() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m WHERE m.actif = true", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compter le nombre de médecins par spécialité
     */
    public long countBySpecialite(Specialite specialite) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m WHERE m.specialite = :specialite", Long.class);
        query.setParameter("specialite", specialite);
        return query.getSingleResult();
    }

    /**
     * Compter le nombre de médecins par spécialité (par ID)
     */
    public long countBySpecialiteId(Long specialiteId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m WHERE m.specialite.id = :specialiteId", Long.class);
        query.setParameter("specialiteId", specialiteId);
        return query.getSingleResult();
    }

    /**
     * Supprimer (désactiver) un médecin - soft delete
     */
    public void delete(Long id) {
        Medecin medecin = findById(id);
        if (medecin != null) {
            medecin.setActif(false);
            entityManager.merge(medecin);
        }
    }

    /**
     * Supprimer définitivement un médecin
     */
    public void hardDelete(Long id) {
        Medecin medecin = findById(id);
        if (medecin != null) {
            entityManager.remove(medecin);
        }
    }

    /**
     * Rechercher des médecins par nom, prénom, email ou spécialité
     */
    public List<Medecin> search(String keyword) {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT DISTINCT m FROM Medecin m " +
                        "LEFT JOIN m.specialite s " +
                        "WHERE LOWER(m.nom) LIKE LOWER(:keyword) " +
                        "OR LOWER(m.prenom) LIKE LOWER(:keyword) " +
                        "OR LOWER(m.email) LIKE LOWER(:keyword) " +
                        "OR LOWER(m.numeroLicence) LIKE LOWER(:keyword) " +
                        "OR LOWER(s.nom) LIKE LOWER(:keyword) " +
                        "ORDER BY m.nom, m.prenom",
                Medecin.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    /**
     * Mettre à jour la dernière connexion
     */
    public void updateLastLogin(Long id) {
        Medecin medecin = findById(id);
        if (medecin != null) {
            medecin.setDerniereConnexion(LocalDateTime.now());
            entityManager.merge(medecin);
        }
    }

    /**
     * Trouver les médecins créés après une certaine date
     */
    public List<Medecin> findCreatedAfter(LocalDateTime date) {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT m FROM Medecin m WHERE m.dateCreation > :date ORDER BY m.dateCreation DESC",
                Medecin.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    /**
     * Trouver les médecins par cabinet
     */
    public List<Medecin> findByCabinet(String cabinet) {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT m FROM Medecin m WHERE m.cabinet = :cabinet ORDER BY m.nom, m.prenom",
                Medecin.class);
        query.setParameter("cabinet", cabinet);
        return query.getResultList();
    }

    /**
     * Trouver les médecins disponibles un jour spécifique
     */
    public List<Medecin> findDisponiblesLeJour(String jour) {
        TypedQuery<Medecin> query = entityManager.createQuery(
                "SELECT DISTINCT m FROM Medecin m JOIN m.joursDisponibles j WHERE j = :jour AND m.actif = true ORDER BY m.nom, m.prenom",
                Medecin.class);
        query.setParameter("jour", jour);
        return query.getResultList();
    }
}
package com.clinique.dao;

import com.clinique.entity.Specialite;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SpecialiteDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sauvegarder une spécialité
     */
    public Specialite save(Specialite specialite) {
        if (specialite.getId() == null) {
            entityManager.persist(specialite);
            return specialite;
        } else {
            return entityManager.merge(specialite);
        }
    }

    /**
     * Trouver une spécialité par son ID
     */
    public Specialite findById(Long id) {
        return entityManager.find(Specialite.class, id);
    }

    /**
     * Trouver une spécialité par son nom
     */
    public Specialite findByNom(String nom) {
        try {
            TypedQuery<Specialite> query = entityManager.createQuery(
                    "SELECT s FROM Specialite s WHERE s.nom = :nom",
                    Specialite.class);
            query.setParameter("nom", nom);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouver une spécialité par son code
     */
    public Specialite findByCode(String code) {
        try {
            TypedQuery<Specialite> query = entityManager.createQuery(
                    "SELECT s FROM Specialite s WHERE s.code = :code",
                    Specialite.class);
            query.setParameter("code", code);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouver toutes les spécialités
     */
    public List<Specialite> findAll() {
        TypedQuery<Specialite> query = entityManager.createQuery(
                "SELECT s FROM Specialite s ORDER BY s.nom",
                Specialite.class);
        return query.getResultList();
    }

    /**
     * Trouver toutes les spécialités actives
     */
    public List<Specialite> findAllActives() {
        TypedQuery<Specialite> query = entityManager.createQuery(
                "SELECT s FROM Specialite s WHERE s.actif = true ORDER BY s.nom",
                Specialite.class);
        return query.getResultList();
    }

    /**
     * Vérifier si un nom existe déjà
     */
    public boolean existsByNom(String nom) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Specialite s WHERE s.nom = :nom", Long.class);
        query.setParameter("nom", nom);
        return query.getSingleResult() > 0;
    }

    /**
     * Vérifier si un code existe déjà
     */
    public boolean existsByCode(String code) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Specialite s WHERE s.code = :code", Long.class);
        query.setParameter("code", code);
        return query.getSingleResult() > 0;
    }

    /**
     * Compter le nombre total de spécialités
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Specialite s", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compter le nombre de spécialités actives
     */
    public long countActives() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Specialite s WHERE s.actif = true", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compter le nombre de médecins par spécialité
     */
    public long countMedecinsBySpecialite(Long specialiteId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM Medecin m WHERE m.specialite.id = :specialiteId", Long.class);
        query.setParameter("specialiteId", specialiteId);
        return query.getSingleResult();
    }

    /**
     * Supprimer définitivement une spécialité
     */
    public void hardDelete(Long id) {
        Specialite specialite = findById(id);
        if (specialite != null) {
            entityManager.remove(specialite);
        }
    }

    /**
     * Rechercher des spécialités par mot-clé
     */
    public List<Specialite> search(String keyword) {
        TypedQuery<Specialite> query = entityManager.createQuery(
                "SELECT s FROM Specialite s WHERE " +
                        "LOWER(s.nom) LIKE LOWER(:keyword) OR " +
                        "LOWER(s.code) LIKE LOWER(:keyword) OR " +
                        "LOWER(s.description) LIKE LOWER(:keyword) " +
                        "ORDER BY s.nom",
                Specialite.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    /**
     * Obtenir les statistiques d'une spécialité
     */
    public Object getSpecialiteStats(Long id) {
        TypedQuery<Object[]> query = entityManager.createQuery(
                "SELECT s.nom, COUNT(m) as nbMedecins " +
                        "FROM Specialite s LEFT JOIN s.medecins m " +
                        "WHERE s.id = :id " +
                        "GROUP BY s.id, s.nom", Object[].class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    /**
     * Trouver les médecins par spécialité
     */
    public List<Object[]> findMedecinsBySpecialite(Long specialiteId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
                "SELECT m.id, m.nom, m.prenom, m.email, m.numeroLicence " +
                        "FROM Medecin m WHERE m.specialite.id = :specialiteId " +
                        "ORDER BY m.nom, m.prenom", Object[].class);
        query.setParameter("specialiteId", specialiteId);
        return query.getResultList();
    }
}
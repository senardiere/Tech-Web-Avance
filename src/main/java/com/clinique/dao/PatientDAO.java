package com.clinique.dao;

import com.clinique.entity.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class PatientDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sauvegarder un patient
     */
    public Patient save(Patient patient) {
        if (patient.getId() == null) {
            entityManager.persist(patient);
            return patient;
        } else {
            return entityManager.merge(patient);
        }
    }

    /**
     * Trouver un patient par son ID
     */
    public Patient findById(Long id) {
        return entityManager.find(Patient.class, id);
    }

    /**
     * Trouver tous les patients
     */
    public List<Patient> findAll() {
        TypedQuery<Patient> query = entityManager.createQuery(
                "SELECT p FROM Patient p ORDER BY p.nom, p.prenom",
                Patient.class);
        return query.getResultList();
    }

    /**
     * Trouver tous les patients actifs
     */
    public List<Patient> findAllActifs() {
        TypedQuery<Patient> query = entityManager.createQuery(
                "SELECT p FROM Patient p WHERE p.statut = 'ACTIF' ORDER BY p.nom, p.prenom",
                Patient.class);
        return query.getResultList();
    }

    /**
     * Compter le nombre total de patients
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Patient p", Long.class);
        return query.getSingleResult();
    }

    /**
     * Supprimer un patient
     */
    public void delete(Long id) {
        Patient patient = findById(id);
        if (patient != null) {
            patient.setStatut(com.clinique.enums.StatutPatient.INACTIF);
            entityManager.merge(patient);
        }
    }

    /**
     * Rechercher des patients par nom
     */
    public List<Patient> rechercherParNom(String nom) {
        TypedQuery<Patient> query = entityManager.createQuery(
                "SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE LOWER(:nom) ORDER BY p.nom, p.prenom",
                Patient.class);
        query.setParameter("nom", "%" + nom + "%");
        return query.getResultList();
    }

    /**
     * Trouver les derniers patients
     */
    public List<Patient> findLastPatients(int limit) {
        TypedQuery<Patient> query = entityManager.createQuery(
                "SELECT p FROM Patient p ORDER BY p.dateCreation DESC",
                Patient.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Compter les patients par médecin
     */
    public long countByMedecinId(Long medecinId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Patient p WHERE p.medecinTraitant.id = :medecinId",
                Long.class);
        query.setParameter("medecinId", medecinId);
        return query.getSingleResult();
    }

    /**
     * Trouver les patients par médecin
     */
    public List<Patient> findByMedecinId(Long medecinId) {
        TypedQuery<Patient> query = entityManager.createQuery(
                "SELECT p FROM Patient p WHERE p.medecinTraitant.id = :medecinId ORDER BY p.nom, p.prenom",
                Patient.class);
        query.setParameter("medecinId", medecinId);
        return query.getResultList();
    }

    /**
     * Trouver les derniers patients d'un médecin
     */
    public List<Patient> findLastByMedecinId(Long medecinId, int limit) {
        TypedQuery<Patient> query = entityManager.createQuery(
                "SELECT p FROM Patient p WHERE p.medecinTraitant.id = :medecinId ORDER BY p.dateCreation DESC",
                Patient.class);
        query.setParameter("medecinId", medecinId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
// Ajoutez cette méthode dans votre PatientDAO.java existant

    /**
     * Compter les patients d'un médecin entre deux dates
     */
    public long countByMedecinAndDateBetween(Long medecinId, LocalDate debut, LocalDate fin) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Patient p WHERE p.medecinTraitant.id = :medecinId " +
                        "AND p.dateCreation BETWEEN :debut AND :fin",
                Long.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("debut", debut.atStartOfDay());
        query.setParameter("fin", fin.plusDays(1).atStartOfDay());
        return query.getSingleResult();
    }
    /**
     * Rechercher des patients par mot-clé pour un médecin
     */
    public List<Patient> searchByMedecin(String keyword, Long medecinId) {
        TypedQuery<Patient> query = entityManager.createQuery(
                "SELECT p FROM Patient p WHERE p.medecinTraitant.id = :medecinId AND " +
                        "(LOWER(p.nom) LIKE LOWER(:keyword) OR LOWER(p.prenom) LIKE LOWER(:keyword) OR " +
                        "LOWER(p.email) LIKE LOWER(:keyword)) ORDER BY p.nom, p.prenom",
                Patient.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }
}
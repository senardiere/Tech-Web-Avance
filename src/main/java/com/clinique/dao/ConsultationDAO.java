package com.clinique.dao;

import com.clinique.entity.Consultation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class ConsultationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sauvegarder une consultation
     */
    public Consultation save(Consultation consultation) {
        if (consultation.getId() == null) {
            entityManager.persist(consultation);
            return consultation;
        } else {
            return entityManager.merge(consultation);
        }
    }

    /**
     * Trouver une consultation par son ID
     */
    public Consultation findById(Long id) {
        return entityManager.find(Consultation.class, id);
    }

    /**
     * Trouver toutes les consultations
     */
    public List<Consultation> findAll() {
        TypedQuery<Consultation> query = entityManager.createQuery(
                "SELECT c FROM Consultation c ORDER BY c.dateConsultation DESC",
                Consultation.class);
        return query.getResultList();
    }

    /**
     * Trouver les consultations par patient
     */
    public List<Consultation> findByPatientId(Long patientId) {
        TypedQuery<Consultation> query = entityManager.createQuery(
                "SELECT c FROM Consultation c WHERE c.patient.id = :patientId ORDER BY c.dateConsultation DESC",
                Consultation.class);
        query.setParameter("patientId", patientId);
        return query.getResultList();
    }

    /**
     * Trouver les consultations par médecin
     */
    public List<Consultation> findByMedecinId(Long medecinId) {
        TypedQuery<Consultation> query = entityManager.createQuery(
                "SELECT c FROM Consultation c WHERE c.medecin.id = :medecinId ORDER BY c.dateConsultation DESC",
                Consultation.class);
        query.setParameter("medecinId", medecinId);
        return query.getResultList();
    }

    /**
     * Compter le nombre total de consultations
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Consultation c", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compter les consultations d'un médecin
     */
    public long countByMedecin(Long medecinId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Consultation c WHERE c.medecin.id = :medecinId",
                Long.class);
        query.setParameter("medecinId", medecinId);
        return query.getSingleResult();
    }

    /**
     * Compter les consultations d'un médecin sur une période
     */
    public long countByMedecinAndPeriode(Long medecinId, LocalDateTime debut, LocalDateTime fin) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Consultation c WHERE c.medecin.id = :medecinId " +
                        "AND c.dateConsultation BETWEEN :debut AND :fin",
                Long.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getSingleResult();
    }

    /**
     * Trouver les dernières consultations d'un médecin
     */
    public List<Consultation> findLastByMedecin(Long medecinId, int limit) {
        TypedQuery<Consultation> query = entityManager.createQuery(
                "SELECT c FROM Consultation c WHERE c.medecin.id = :medecinId ORDER BY c.dateConsultation DESC",
                Consultation.class);
        query.setParameter("medecinId", medecinId);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Trouver les consultations d'un patient avec un médecin spécifique
     */
    public List<Consultation> findByPatientAndMedecin(Long patientId, Long medecinId) {
        TypedQuery<Consultation> query = entityManager.createQuery(
                "SELECT c FROM Consultation c WHERE c.patient.id = :patientId AND c.medecin.id = :medecinId " +
                        "ORDER BY c.dateConsultation DESC",
                Consultation.class);
        query.setParameter("patientId", patientId);
        query.setParameter("medecinId", medecinId);
        return query.getResultList();
    }

    /**
     * Supprimer une consultation
     */
    public void delete(Long id) {
        Consultation consultation = findById(id);
        if (consultation != null) {
            entityManager.remove(consultation);
        }
    }
}
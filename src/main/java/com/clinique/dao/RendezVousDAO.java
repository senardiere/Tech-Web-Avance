package com.clinique.dao;

import com.clinique.entity.RendezVous;
import com.clinique.enums.StatutRendezVous;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class RendezVousDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sauvegarder un rendez-vous
     */
    public RendezVous save(RendezVous rendezVous) {
        if (rendezVous.getId() == null) {
            entityManager.persist(rendezVous);
            return rendezVous;
        } else {
            return entityManager.merge(rendezVous);
        }
    }

    /**
     * Trouver un rendez-vous par son ID
     */
    public RendezVous findById(Long id) {
        return entityManager.find(RendezVous.class, id);
    }

    /**
     * Trouver tous les rendez-vous
     */
    public List<RendezVous> findAll() {
        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r ORDER BY r.dateHeure DESC",
                RendezVous.class);
        return query.getResultList();
    }

    /**
     * Trouver les rendez-vous par médecin
     */
    public List<RendezVous> findByMedecinId(Long medecinId) {
        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId ORDER BY r.dateHeure",
                RendezVous.class);
        query.setParameter("medecinId", medecinId);
        return query.getResultList();
    }

    /**
     * Trouver les rendez-vous d'un médecin pour une date
     */
    public List<RendezVous> findByMedecinIdAndDate(Long medecinId, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.plusDays(1).atStartOfDay();

        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId " +
                        "AND r.dateHeure BETWEEN :debut AND :fin ORDER BY r.dateHeure",
                RendezVous.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getResultList();
    }

    /**
     * Trouver les rendez-vous d'un médecin sur une période
     */
    public List<RendezVous> findByMedecinAndPeriode(Long medecinId, LocalDateTime debut, LocalDateTime fin) {
        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId " +
                        "AND r.dateHeure BETWEEN :debut AND :fin ORDER BY r.dateHeure",
                RendezVous.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getResultList();
    }

    /**
     * Trouver les rendez-vous par patient
     */
    public List<RendezVous> findByPatientId(Long patientId) {
        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r WHERE r.patient.id = :patientId ORDER BY r.dateHeure DESC",
                RendezVous.class);
        query.setParameter("patientId", patientId);
        return query.getResultList();
    }

    /**
     * Trouver les rendez-vous par statut
     */
    public List<RendezVous> findByStatut(StatutRendezVous statut) {
        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r WHERE r.statut = :statut ORDER BY r.dateHeure",
                RendezVous.class);
        query.setParameter("statut", statut);
        return query.getResultList();
    }

    /**
     * Trouver les rendez-vous du jour
     */
    public List<RendezVous> findToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime debut = today.atStartOfDay();
        LocalDateTime fin = today.plusDays(1).atStartOfDay();

        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin ORDER BY r.dateHeure",
                RendezVous.class);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getResultList();
    }

    /**
     * Compter le nombre total de rendez-vous
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM RendezVous r", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compter les rendez-vous d'un médecin sur une période
     */
    public long countByMedecinAndPeriode(Long medecinId, LocalDateTime debut, LocalDateTime fin) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :medecinId " +
                        "AND r.dateHeure BETWEEN :debut AND :fin",
                Long.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getSingleResult();
    }

    /**
     * Vérifier la disponibilité d'un créneau - VERSION CORRIGÉE AVEC VÉRIFICATION EN MÉMOIRE
     */
    public boolean isCreneauDisponible(Long medecinId, LocalDateTime dateHeure, int duree) {
        LocalDateTime fin = dateHeure.plusMinutes(duree);

        // Récupérer tous les rendez-vous du médecin pour la journée (et un peu avant/après)
        LocalDateTime debutRecherche = dateHeure.minusDays(1);
        LocalDateTime finRecherche = fin.plusDays(1);

        String jpql = "SELECT r FROM RendezVous r " +
                "WHERE r.medecin.id = :medecinId " +
                "AND r.statut != :statutAnnule " +
                "AND r.dateHeure BETWEEN :debutRecherche AND :finRecherche";

        TypedQuery<RendezVous> query = entityManager.createQuery(jpql, RendezVous.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("statutAnnule", StatutRendezVous.ANNULE);
        query.setParameter("debutRecherche", debutRecherche);
        query.setParameter("finRecherche", finRecherche);

        List<RendezVous> rendezVousExistants = query.getResultList();

        // Vérifier manuellement le chevauchement en Java
        for (RendezVous rdv : rendezVousExistants) {
            LocalDateTime rdvDebut = rdv.getDateHeure();
            LocalDateTime rdvFin = rdvDebut.plusMinutes(rdv.getDuree() != null ? rdv.getDuree() : 30);

            // Vérifier si les créneaux se chevauchent
            if (dateHeure.isBefore(rdvFin) && fin.isAfter(rdvDebut)) {
                return false; // Non disponible
            }
        }

        return true; // Disponible
    }

    /**
     * Vérifier la disponibilité d'un créneau - Version simplifiée
     */
    public boolean isCreneauDisponibleSimple(Long medecinId, LocalDateTime dateHeure, int duree) {
        LocalDateTime fin = dateHeure.plusMinutes(duree);

        String jpql = "SELECT r FROM RendezVous r " +
                "WHERE r.medecin.id = :medecinId " +
                "AND r.statut != :statutAnnule " +
                "AND r.dateHeure >= :debut " +
                "AND r.dateHeure <= :fin";

        TypedQuery<RendezVous> query = entityManager.createQuery(jpql, RendezVous.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("statutAnnule", StatutRendezVous.ANNULE);
        query.setParameter("debut", dateHeure.minusMinutes(30));
        query.setParameter("fin", fin.plusMinutes(30));

        List<RendezVous> rendezVousExistants = query.getResultList();

        for (RendezVous rdv : rendezVousExistants) {
            LocalDateTime rdvDebut = rdv.getDateHeure();
            LocalDateTime rdvFin = rdvDebut.plusMinutes(rdv.getDuree() != null ? rdv.getDuree() : 30);

            if (dateHeure.isBefore(rdvFin) && fin.isAfter(rdvDebut)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Trouver les prochains rendez-vous d'un médecin
     */
    public List<RendezVous> findProchainsByMedecin(Long medecinId, int limit) {
        String jpql = "SELECT r FROM RendezVous r " +
                "WHERE r.medecin.id = :medecinId " +
                "AND r.dateHeure > CURRENT_TIMESTAMP " +
                "AND r.statut != :statutAnnule " +
                "ORDER BY r.dateHeure";

        TypedQuery<RendezVous> query = entityManager.createQuery(jpql, RendezVous.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("statutAnnule", StatutRendezVous.ANNULE);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Compter les rendez-vous d'un médecin pour une date
     */
    public long countByMedecinAndDate(Long medecinId, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.plusDays(1).atStartOfDay();

        String jpql = "SELECT COUNT(r) FROM RendezVous r " +
                "WHERE r.medecin.id = :medecinId " +
                "AND r.dateHeure BETWEEN :debut AND :fin";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getSingleResult();
    }

    /**
     * Trouver les rendez-vous d'un patient avec un médecin spécifique
     */
    public List<RendezVous> findByPatientAndMedecin(Long patientId, Long medecinId) {
        String jpql = "SELECT r FROM RendezVous r " +
                "WHERE r.patient.id = :patientId " +
                "AND r.medecin.id = :medecinId " +
                "ORDER BY r.dateHeure DESC";

        TypedQuery<RendezVous> query = entityManager.createQuery(jpql, RendezVous.class);
        query.setParameter("patientId", patientId);
        query.setParameter("medecinId", medecinId);
        return query.getResultList();
    }

    /**
     * Supprimer un rendez-vous
     */
    public void delete(Long id) {
        RendezVous rendezVous = findById(id);
        if (rendezVous != null) {
            entityManager.remove(rendezVous);
        }
    }
// Ajoutez ces méthodes dans votre RendezVousDAO.java existant

    /**
     * Trouver les rendez-vous par période
     */
    public List<RendezVous> findByPeriode(LocalDateTime debut, LocalDateTime fin) {
        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin ORDER BY r.dateHeure",
                RendezVous.class);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getResultList();
    }

    /**
     * Compter les rendez-vous par période
     */
    public long countByPeriode(LocalDateTime debut, LocalDateTime fin) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin",
                Long.class);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getSingleResult();
    }

    /**
     * Compter les rendez-vous par statut
     */
    public long countByStatut(StatutRendezVous statut) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM RendezVous r WHERE r.statut = :statut",
                Long.class);
        query.setParameter("statut", statut);
        return query.getSingleResult();
    }

    /**
     * Compter les rendez-vous d'un médecin par statut
     */
    public long countByMedecinAndStatut(Long medecinId, StatutRendezVous statut) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.statut = :statut",
                Long.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("statut", statut);
        return query.getSingleResult();
    }

    /**
     * Trouver les rendez-vous d'un médecin par statut
     */
    public List<RendezVous> findByMedecinAndStatut(Long medecinId, StatutRendezVous statut) {
        TypedQuery<RendezVous> query = entityManager.createQuery(
                "SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.statut = :statut ORDER BY r.dateHeure",
                RendezVous.class);
        query.setParameter("medecinId", medecinId);
        query.setParameter("statut", statut);
        return query.getResultList();
    }
    /**
     * Mettre à jour le statut d'un rendez-vous
     */
    public void updateStatut(Long id, StatutRendezVous statut) {
        RendezVous rendezVous = findById(id);
        if (rendezVous != null) {
            rendezVous.setStatut(statut);
            entityManager.merge(rendezVous);
        }
    }
}
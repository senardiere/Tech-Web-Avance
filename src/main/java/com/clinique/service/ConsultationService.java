package com.clinique.service;

import com.clinique.dao.ConsultationDAO;
import com.clinique.entity.Consultation;
import com.clinique.entity.RendezVous;
import com.clinique.enums.StatutConsultation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ConsultationService {

    @Autowired
    private ConsultationDAO consultationDAO;

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AuditService auditService;

    /**
     * Créer une nouvelle consultation
     */
    public Consultation createConsultation(Consultation consultation) {
        if (consultation.getPatient() == null || consultation.getPatient().getId() == null) {
            throw new RuntimeException("Le patient est obligatoire");
        }
        if (consultation.getMedecin() == null || consultation.getMedecin().getId() == null) {
            throw new RuntimeException("Le médecin est obligatoire");
        }

        consultation.setDateConsultation(LocalDateTime.now());
        consultation.setStatut(StatutConsultation.TERMINEE);

        if (consultation.getRendezVous() != null && consultation.getRendezVous().getId() != null) {
            RendezVous rdv = rendezVousService.getRendezVousById(consultation.getRendezVous().getId());
            if (rdv != null) {
                rendezVousService.validerRendezVous(rdv.getId());
            }
        }

        Consultation saved = consultationDAO.save(consultation);

        patientService.updateDerniereVisite(
                consultation.getPatient().getId(),
                LocalDate.now()
        );

        auditService.log("CREATE", "Consultation", saved.getId(),
                "Consultation créée pour " + saved.getPatient().getPrenom() + " " +
                        saved.getPatient().getNom());

        return saved;
    }

    /**
     * Créer une consultation à partir d'un rendez-vous
     */
    public Consultation createConsultationFromRendezVous(Long rendezVousId) {
        RendezVous rdv = rendezVousService.getRendezVousById(rendezVousId);
        if (rdv == null) {
            throw new RuntimeException("Rendez-vous non trouvé");
        }

        Consultation consultation = new Consultation();
        consultation.setPatient(rdv.getPatient());
        consultation.setMedecin(rdv.getMedecin());
        consultation.setRendezVous(rdv);
        consultation.setDateConsultation(LocalDateTime.now());
        consultation.setStatut(StatutConsultation.EN_COURS);

        return consultationDAO.save(consultation);
    }

    /**
     * Créer une consultation directe (sans rendez-vous)
     */
    public Consultation createConsultationDirecte(Consultation consultation) {
        consultation.setDateConsultation(LocalDateTime.now());
        consultation.setStatut(StatutConsultation.EN_COURS);

        Consultation saved = consultationDAO.save(consultation);

        patientService.updateDerniereVisite(
                consultation.getPatient().getId(),
                LocalDate.now()
        );

        return saved;
    }

    /**
     * Terminer une consultation
     */
    public void terminerConsultation(Long id) {
        Consultation consultation = getConsultationById(id);
        if (consultation == null) {
            throw new RuntimeException("Consultation non trouvée");
        }
        consultation.setStatut(StatutConsultation.TERMINEE);
        consultationDAO.save(consultation);

        auditService.log("COMPLETE", "Consultation", id, "Consultation terminée");
    }

    /**
     * Récupérer toutes les consultations
     */
    @Transactional(readOnly = true)
    public List<Consultation> getAllConsultations() {
        return consultationDAO.findAll();
    }

    /**
     * Récupérer une consultation par son ID
     */
    @Transactional(readOnly = true)
    public Consultation getConsultationById(Long id) {
        return consultationDAO.findById(id);
    }

    /**
     * Récupérer les consultations d'un patient
     */
    @Transactional(readOnly = true)
    public List<Consultation> getConsultationsByPatient(Long patientId) {
        return consultationDAO.findByPatientId(patientId);
    }

    /**
     * Récupérer les consultations d'un médecin
     */
    @Transactional(readOnly = true)
    public List<Consultation> getConsultationsByMedecin(Long medecinId) {
        return consultationDAO.findByMedecinId(medecinId);
    }

    /**
     * Compter le nombre total de consultations
     */
    @Transactional(readOnly = true)
    public long countConsultations() {
        return consultationDAO.count();
    }

    /**
     * Compter les consultations d'un médecin
     */
    @Transactional(readOnly = true)
    public long countConsultationsByMedecin(Long medecinId) {
        return consultationDAO.countByMedecin(medecinId);
    }

    /**
     * Récupérer les dernières consultations d'un médecin
     */
    @Transactional(readOnly = true)
    public List<Consultation> getDernieresConsultationsByMedecin(Long medecinId, int limit) {
        return consultationDAO.findLastByMedecin(medecinId, limit);
    }

    /**
     * Récupérer les consultations d'un patient avec un médecin spécifique
     */
    @Transactional(readOnly = true)
    public List<Consultation> getConsultationsByPatientAndMedecin(Long patientId, Long medecinId) {
        return consultationDAO.findByPatientAndMedecin(patientId, medecinId);
    }

    /**
     * Compter les consultations d'un médecin entre deux dates
     */
    @Transactional(readOnly = true)
    public long countConsultationsByMedecinAndDateBetween(Long medecinId, LocalDate debut, LocalDate fin) {
        LocalDateTime debutDateTime = debut.atStartOfDay();
        LocalDateTime finDateTime = fin.plusDays(1).atStartOfDay();
        return consultationDAO.countByMedecinAndPeriode(medecinId, debutDateTime, finDateTime);
    }

    /**
     * Mettre à jour une consultation
     */
    public Consultation updateConsultation(Consultation consultation) {
        Consultation updated = consultationDAO.save(consultation);
        auditService.log("UPDATE", "Consultation", updated.getId(), "Consultation mise à jour");
        return updated;
    }

    /**
     * Supprimer une consultation
     */
    public void deleteConsultation(Long id) {
        Consultation consultation = getConsultationById(id);
        if (consultation != null) {
            consultationDAO.delete(id);
            auditService.log("DELETE", "Consultation", id, "Consultation supprimée");
        }
    }
}
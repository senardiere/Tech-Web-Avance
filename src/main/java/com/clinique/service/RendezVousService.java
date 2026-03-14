package com.clinique.service;

import com.clinique.dao.RendezVousDAO;
import com.clinique.entity.Medecin;
import com.clinique.entity.Patient;
import com.clinique.entity.RendezVous;
import com.clinique.enums.StatutRendezVous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RendezVousService {

    @Autowired
    private RendezVousDAO rendezVousDAO;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedecinService medecinService;

    @Autowired
    private AuditService auditService;

    // ========== MÉTHODES DE RECHERCHE ==========

    @Transactional(readOnly = true)
    public List<RendezVous> getAllRendezVous() {
        return rendezVousDAO.findAll();
    }

    @Transactional(readOnly = true)
    public RendezVous getRendezVousById(Long id) {
        return rendezVousDAO.findById(id);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByMedecin(Long medecinId) {
        return rendezVousDAO.findByMedecinId(medecinId);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        return rendezVousDAO.findByMedecinIdAndDate(medecinId, date);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByMedecinAndStatut(Long medecinId, StatutRendezVous statut) {
        return rendezVousDAO.findByMedecinAndStatut(medecinId, statut);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByPatient(Long patientId) {
        return rendezVousDAO.findByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByStatut(StatutRendezVous statut) {
        return rendezVousDAO.findByStatut(statut);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByDate(LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.plusDays(1).atStartOfDay();
        return rendezVousDAO.findByPeriode(debut, fin);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousBetweenDates(LocalDate debut, LocalDate fin) {
        LocalDateTime debutDateTime = debut.atStartOfDay();
        LocalDateTime finDateTime = fin.plusDays(1).atStartOfDay();
        return rendezVousDAO.findByPeriode(debutDateTime, finDateTime);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousDuJour() {
        return rendezVousDAO.findToday();
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByMedecinAndDateBetween(
            Long medecinId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime debut = startDate.atStartOfDay();
        LocalDateTime fin = endDate.plusDays(1).atStartOfDay();
        return rendezVousDAO.findByMedecinAndPeriode(medecinId, debut, fin);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByPatientAndMedecin(Long patientId, Long medecinId) {
        return rendezVousDAO.findByPatientAndMedecin(patientId, medecinId);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getProchainsRendezVousPatient(Long patientId) {
        List<RendezVous> tous = rendezVousDAO.findByPatientId(patientId);
        return tous.stream()
                .filter(r -> r.getDateHeure().isAfter(LocalDateTime.now()))
                .filter(r -> r.getStatut() != StatutRendezVous.ANNULE)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getProchainsRendezVousMedecin(Long medecinId) {
        return getProchainsRendezVousMedecin(medecinId, 10);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getProchainsRendezVousMedecin(Long medecinId, int limit) {
        return rendezVousDAO.findProchainsByMedecin(medecinId, limit);
    }

    // ========== MÉTHODES DE COMPTAGE ==========

    @Transactional(readOnly = true)
    public long countRendezVous() {
        return rendezVousDAO.count();
    }

    @Transactional(readOnly = true)
    public long countByStatut(StatutRendezVous statut) {
        return rendezVousDAO.countByStatut(statut);
    }

    @Transactional(readOnly = true)
    public long countRendezVousDuJour() {
        LocalDate today = LocalDate.now();
        LocalDateTime debut = today.atStartOfDay();
        LocalDateTime fin = today.plusDays(1).atStartOfDay();
        return rendezVousDAO.countByPeriode(debut, fin);
    }

    @Transactional(readOnly = true)
    public long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.plusDays(1).atStartOfDay();
        return rendezVousDAO.countByMedecinAndPeriode(medecinId, debut, fin);
    }

    @Transactional(readOnly = true)
    public long countRendezVousByMedecinAndDateBetween(Long medecinId, LocalDate debut, LocalDate fin) {
        LocalDateTime debutDateTime = debut.atStartOfDay();
        LocalDateTime finDateTime = fin.plusDays(1).atStartOfDay();
        return rendezVousDAO.countByMedecinAndPeriode(medecinId, debutDateTime, finDateTime);
    }

    @Transactional(readOnly = true)
    public long countRendezVousByMedecinAndWeek(Long medecinId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return countRendezVousByMedecinAndDateBetween(medecinId, startOfWeek, endOfWeek);
    }

    @Transactional(readOnly = true)
    public long countRendezVousByMedecinAndStatut(Long medecinId, StatutRendezVous statut) {
        return rendezVousDAO.countByMedecinAndStatut(medecinId, statut);
    }

    // ========== MÉTHODES DE CRUD ==========

    @Transactional
    public RendezVous createRendezVous(RendezVous rendezVous) {
        if (rendezVous.getDateHeure() == null) {
            throw new IllegalArgumentException("La date du rendez-vous est obligatoire");
        }

        if (rendezVous.getDateHeure().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Impossible de prendre un rendez-vous dans le passé");
        }

        if (rendezVous.getPatient() == null || rendezVous.getPatient().getId() == null) {
            throw new IllegalArgumentException("Le patient est obligatoire");
        }

        if (rendezVous.getMedecin() == null || rendezVous.getMedecin().getId() == null) {
            throw new IllegalArgumentException("Le médecin est obligatoire");
        }

        Patient patient = patientService.getPatientById(rendezVous.getPatient().getId());
        if (patient == null) {
            throw new RuntimeException("Patient non trouvé");
        }

        Medecin medecin = medecinService.getMedecinById(rendezVous.getMedecin().getId());
        if (medecin == null) {
            throw new RuntimeException("Médecin non trouvé");
        }
        if (!medecin.isActif()) {
            throw new RuntimeException("Ce médecin n'est pas actif");
        }

        boolean disponible = isCreneauDisponible(
                medecin.getId(),
                rendezVous.getDateHeure(),
                rendezVous.getDuree() != null ? rendezVous.getDuree() : 30
        );

        if (!disponible) {
            throw new RuntimeException("Le médecin n'est pas disponible sur ce créneau");
        }

        if (rendezVous.getDuree() == null) {
            rendezVous.setDuree(30);
        }
        if (rendezVous.getStatut() == null) {
            rendezVous.setStatut(StatutRendezVous.PREVU);
        }
        rendezVous.setDatePrise(LocalDateTime.now());

        RendezVous saved = rendezVousDAO.save(rendezVous);

        auditService.log("CREATE", "RendezVous", saved.getId(),
                "Rendez-vous créé: " + saved.getPatient().getPrenom() + " " +
                        saved.getPatient().getNom() + " avec Dr. " + saved.getMedecin().getNom());

        return saved;
    }

    @Transactional
    public void validerRendezVous(Long id) {
        RendezVous rendezVous = rendezVousDAO.findById(id);
        if (rendezVous == null) {
            throw new RuntimeException("Rendez-vous non trouvé");
        }

        if (rendezVous.getStatut() != StatutRendezVous.PREVU) {
            throw new RuntimeException("Seuls les rendez-vous prévus peuvent être validés");
        }

        rendezVous.setStatut(StatutRendezVous.VALIDE);
        rendezVousDAO.save(rendezVous);

        patientService.updateDerniereVisite(rendezVous.getPatient().getId(), LocalDate.now());

        auditService.log("VALIDATE", "RendezVous", id, "Rendez-vous validé");
    }

    @Transactional
    public void annulerRendezVous(Long id, String motif) {
        if (motif == null || motif.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif d'annulation est obligatoire");
        }

        RendezVous rendezVous = rendezVousDAO.findById(id);
        if (rendezVous == null) {
            throw new RuntimeException("Rendez-vous non trouvé");
        }

        if (rendezVous.getStatut() == StatutRendezVous.ANNULE) {
            throw new RuntimeException("Ce rendez-vous est déjà annulé");
        }

        rendezVous.setStatut(StatutRendezVous.ANNULE);
        rendezVous.setMotifAnnulation(motif);
        rendezVousDAO.save(rendezVous);

        auditService.log("CANCEL", "RendezVous", id, "Rendez-vous annulé: " + motif);
    }

    @Transactional
    public void deleteRendezVous(Long id) {
        RendezVous rendezVous = rendezVousDAO.findById(id);
        if (rendezVous == null) {
            throw new RuntimeException("Rendez-vous non trouvé");
        }

        if (rendezVous.getConsultation() != null) {
            throw new RuntimeException("Impossible de supprimer un rendez-vous avec une consultation associée");
        }

        rendezVousDAO.delete(id);

        auditService.log("DELETE", "RendezVous", id, "Rendez-vous supprimé");
    }

    @Transactional(readOnly = true)
    public boolean isCreneauDisponible(Long medecinId, LocalDateTime dateHeure, int duree) {
        return rendezVousDAO.isCreneauDisponible(medecinId, dateHeure, duree);
    }
}
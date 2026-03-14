package com.clinique.controller;

import com.clinique.entity.Medecin;
import com.clinique.entity.Utilisateur;
import com.clinique.enums.StatutRendezVous;
import com.clinique.service.AuthService;
import com.clinique.service.ConsultationService;
import com.clinique.service.PatientService;
import com.clinique.service.RendezVousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/mon-espace")
public class EspaceMedecinController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private ConsultationService consultationService;

    /**
     * Vérifier que l'utilisateur est bien un médecin
     */
    private Medecin getCurrentMedecin() {
        Utilisateur user = authService.getCurrentUser();
        if (user instanceof Medecin) {
            return (Medecin) user;
        }
        return null;
    }

    /**
     * Dashboard du médecin
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/dashboard";
        }

        Long medecinId = medecin.getId();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Infos du médecin
        model.addAttribute("medecin", medecin);
        model.addAttribute("dateToday", today.format(formatter));

        // Statistiques
        model.addAttribute("totalPatients", patientService.countPatientsByMedecin(medecinId));
        model.addAttribute("totalRdvAujourdhui",
                rendezVousService.countRendezVousByMedecinAndDate(medecinId, today));
        model.addAttribute("totalRdvSemaine",
                rendezVousService.countRendezVousByMedecinAndWeek(medecinId));
        model.addAttribute("totalConsultations",
                consultationService.countConsultationsByMedecin(medecinId));

        // Rendez-vous du jour
        model.addAttribute("rdvDuJour",
                rendezVousService.getRendezVousByMedecinAndDate(medecinId, today));

        // Prochains rendez-vous (5 prochains)
        model.addAttribute("prochainsRdv",
                rendezVousService.getProchainsRendezVousMedecin(medecinId, 5));

        // Derniers patients (10 derniers)
        model.addAttribute("derniersPatients",
                patientService.getDerniersPatientsByMedecin(medecinId, 10));

        // Dernières consultations (5 dernières)
        model.addAttribute("dernieresConsultations",
                consultationService.getDernieresConsultationsByMedecin(medecinId, 5));

        return "medecins/dashboard";
    }

    /**
     * Liste des patients du médecin
     */
    @GetMapping("/mes-patients")
    public String mesPatients(@RequestParam(required = false) String keyword, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/patients";
        }

        List<com.clinique.entity.Patient> patients;
        if (keyword != null && !keyword.isEmpty()) {
            patients = patientService.searchPatientsByMedecin(keyword, medecin.getId());
            model.addAttribute("keyword", keyword);
        } else {
            patients = patientService.getPatientsByMedecin(medecin.getId());
        }

        model.addAttribute("patients", patients);
        model.addAttribute("medecin", medecin);

        return "medecins/patients";
    }

    /**
     * Liste des rendez-vous du médecin (AFFICHAGE SEULEMENT)
     */
    @GetMapping("/mes-rendezvous")
    public String mesRendezVous(@RequestParam(required = false) String date,
                                @RequestParam(required = false) String statut,
                                Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/rendezvous";
        }

        Long medecinId = medecin.getId();
        List<com.clinique.entity.RendezVous> rendezVous;

        // Application des filtres
        if (date != null && !date.isEmpty()) {
            LocalDate filterDate = LocalDate.parse(date);
            rendezVous = rendezVousService.getRendezVousByMedecinAndDate(medecinId, filterDate);
        } else if (statut != null && !statut.isEmpty()) {
            StatutRendezVous statutEnum = StatutRendezVous.valueOf(statut);
            rendezVous = rendezVousService.getRendezVousByMedecinAndStatut(medecinId, statutEnum);
        } else {
            rendezVous = rendezVousService.getRendezVousByMedecin(medecinId);
        }

        // Statistiques
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Premier et dernier jour du mois
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        model.addAttribute("totalRdvAujourdhui",
                rendezVousService.countRendezVousByMedecinAndDate(medecinId, today));
        model.addAttribute("totalRdvSemaine",
                rendezVousService.countRendezVousByMedecinAndDateBetween(medecinId, startOfWeek, endOfWeek));
        model.addAttribute("totalRdvMois",
                rendezVousService.countRendezVousByMedecinAndDateBetween(medecinId, firstDayOfMonth, lastDayOfMonth));

        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("medecin", medecin);
        model.addAttribute("dateFilter", date);
        model.addAttribute("statutFilter", statut);

        return "medecins/mes-rendezvous";
    }

    /**
     * Agenda du médecin (vue hebdomadaire)
     */
    @GetMapping("/mon-agenda")
    public String monAgenda(@RequestParam(required = false) String semaine, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/dashboard";
        }

        // Calcul de la semaine (courante ou demandée)
        LocalDate startOfWeek;
        if (semaine != null && !semaine.isEmpty()) {
            startOfWeek = LocalDate.parse(semaine);
        } else {
            startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        }

        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Récupérer les rendez-vous de la semaine
        model.addAttribute("rdvSemaine",
                rendezVousService.getRendezVousByMedecinAndDateBetween(
                        medecin.getId(), startOfWeek, endOfWeek));

        // Informations pour la navigation
        model.addAttribute("startOfWeek", startOfWeek);
        model.addAttribute("endOfWeek", endOfWeek);
        model.addAttribute("semainePrecedente", startOfWeek.minusWeeks(1));
        model.addAttribute("semaineSuivante", startOfWeek.plusWeeks(1));
        model.addAttribute("medecin", medecin);

        return "medecins/agenda";
    }

    /**
     * Détails d'un patient pour le médecin
     */
    @GetMapping("/patient/{id}")
    public String voirPatient(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/patients";
        }

        com.clinique.entity.Patient patient = patientService.getPatientById(id);

        // Vérifier que ce patient appartient bien à ce médecin
        if (!patient.getMedecinTraitant().getId().equals(medecin.getId())) {
            return "redirect:/mon-espace/mes-patients";
        }

        model.addAttribute("patient", patient);
        model.addAttribute("consultations",
                consultationService.getConsultationsByPatientAndMedecin(id, medecin.getId()));
        model.addAttribute("rendezVous",
                rendezVousService.getRendezVousByPatientAndMedecin(id, medecin.getId()));
        model.addAttribute("medecin", medecin);

        return "medecins/patient-details";
    }

    /**
     * Recherche de patients
     */
    @GetMapping("/rechercher-patient")
    public String rechercherPatient(@RequestParam String keyword, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/patients";
        }

        model.addAttribute("patients",
                patientService.searchPatientsByMedecin(keyword, medecin.getId()));
        model.addAttribute("keyword", keyword);
        model.addAttribute("medecin", medecin);

        return "medecins/patients";
    }

    /**
     * Formulaire de nouveau rendez-vous (seulement accessible mais non utilisable)
     * Cette méthode existe pour la navigation mais le médecin ne peut pas créer de RDV
     */
    @GetMapping("/rendezvous/nouveau")
    public String nouveauRendezVous(@RequestParam(required = false) Long patientId, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/rendezvous";
        }

        // Rediriger vers la liste avec un message
        model.addAttribute("info", "Seul l'administrateur peut créer des rendez-vous");
        return "redirect:/mon-espace/mes-rendezvous";
    }

    /**
     * Formulaire de nouvelle consultation
     */
    @GetMapping("/consultation/nouvelle")
    public String nouvelleConsultation(@RequestParam(required = false) Long rdvId,
                                       @RequestParam(required = false) Long patientId,
                                       Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/consultations";
        }

        // Pré-remplir si un RDV est sélectionné
        if (rdvId != null) {
            com.clinique.entity.RendezVous rdv = rendezVousService.getRendezVousById(rdvId);
            if (rdv != null && rdv.getMedecin().getId().equals(medecin.getId())) {
                model.addAttribute("patient", rdv.getPatient());
                model.addAttribute("rdvId", rdvId);
            }
        } else if (patientId != null) {
            com.clinique.entity.Patient patient = patientService.getPatientById(patientId);
            if (patient != null && patient.getMedecinTraitant().getId().equals(medecin.getId())) {
                model.addAttribute("patient", patient);
            }
        }

        model.addAttribute("medecin", medecin);
        model.addAttribute("consultation", new com.clinique.entity.Consultation());

        return "medecins/nouvelle-consultation";
    }



    /**
     * Statistiques du médecin
     */
    @GetMapping("/statistiques")
    public String statistiques(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = getCurrentMedecin();
        if (medecin == null) {
            return "redirect:/dashboard";
        }

        Long medecinId = medecin.getId();
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // Statistiques mensuelles
        model.addAttribute("totalPatientsMois",
                patientService.countPatientsByMedecinAndDateBetween(medecinId, startOfMonth, endOfMonth));
        model.addAttribute("totalRdvMois",
                rendezVousService.countRendezVousByMedecinAndDateBetween(medecinId, startOfMonth, endOfMonth));
        model.addAttribute("totalConsultationsMois",
                consultationService.countConsultationsByMedecinAndDateBetween(medecinId, startOfMonth, endOfMonth));

        // Répartition par statut
        model.addAttribute("rdvPrevu",
                rendezVousService.countRendezVousByMedecinAndStatut(medecinId, StatutRendezVous.PREVU));
        model.addAttribute("rdvValide",
                rendezVousService.countRendezVousByMedecinAndStatut(medecinId, StatutRendezVous.VALIDE));
        model.addAttribute("rdvAnnule",
                rendezVousService.countRendezVousByMedecinAndStatut(medecinId, StatutRendezVous.ANNULE));

        model.addAttribute("medecin", medecin);

        return "medecins/statistiques";
    }
}
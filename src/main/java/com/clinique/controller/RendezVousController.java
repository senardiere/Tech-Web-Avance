package com.clinique.controller;

import com.clinique.entity.RendezVous;
import com.clinique.entity.Utilisateur;
import com.clinique.enums.StatutRendezVous;
import com.clinique.service.AuthService;
import com.clinique.service.MedecinService;
import com.clinique.service.PatientService;
import com.clinique.service.RendezVousService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/rendezvous")
public class RendezVousController {

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedecinService medecinService;

    @Autowired
    private AuthService authService;

    /**
     * Liste tous les rendez-vous (pour admin)
     */
    @GetMapping
    public String listRendezVous(@RequestParam(required = false) Long medecin,
                                 @RequestParam(required = false) String statut,
                                 @RequestParam(required = false) String date,
                                 Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();

        // Vérifier que c'est un admin
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        List<RendezVous> rendezVous;

        // Application des filtres
        if (medecin != null) {
            rendezVous = rendezVousService.getRendezVousByMedecin(medecin);
        } else if (statut != null && !statut.isEmpty()) {
            rendezVous = rendezVousService.getRendezVousByStatut(StatutRendezVous.valueOf(statut));
        } else if (date != null && !date.isEmpty()) {
            LocalDate filterDate = LocalDate.parse(date);
            rendezVous = rendezVousService.getRendezVousByDate(filterDate);
        } else {
            rendezVous = rendezVousService.getAllRendezVous();
        }

        // Statistiques pour le dashboard
        model.addAttribute("totalRendezVous", rendezVousService.countRendezVous());
        model.addAttribute("totalPrevu", rendezVousService.countByStatut(StatutRendezVous.PREVU));
        model.addAttribute("totalValide", rendezVousService.countByStatut(StatutRendezVous.VALIDE));
        model.addAttribute("totalAnnule", rendezVousService.countByStatut(StatutRendezVous.ANNULE));
        model.addAttribute("rdvAujourdhui", rendezVousService.countRendezVousDuJour());

        // Données pour les filtres
        model.addAttribute("medecins", medecinService.getAllMedecins());
        model.addAttribute("rendezvous", rendezVous);

        return "rendezvous/list";
    }

    /**
     * Affiche le formulaire de création d'un rendez-vous
     */
    @GetMapping("/nouveau")
    public String showForm(@RequestParam(required = false) Long patientId,
                           @RequestParam(required = false) Long medecinId,
                           Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        RendezVous rendezVous = new RendezVous();

        if (patientId != null) {
            rendezVous.setPatient(patientService.getPatientById(patientId));
        }

        if (medecinId != null) {
            rendezVous.setMedecin(medecinService.getMedecinById(medecinId));
        }

        // Ajouter la date du jour pour le champ min du formulaire
        model.addAttribute("today", LocalDate.now().toString());
        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("medecins", medecinService.getAllMedecins());

        return "rendezvous/form";
    }

    /**
     * Sauvegarde un rendez-vous (création ou modification)
     */
    @PostMapping("/save")
    public String saveRendezVous(@Valid @ModelAttribute("rendezVous") RendezVous rendezVous,
                                 @RequestParam String date,
                                 @RequestParam String heure,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        if (result.hasErrors()) {
            return "rendezvous/form";
        }

        try {
            // Combiner date et heure
            LocalDateTime dateHeure = LocalDateTime.parse(date + "T" + heure + ":00");
            rendezVous.setDateHeure(dateHeure);

            // Si c'est une création, le statut est PREVU par défaut
            if (rendezVous.getId() == null) {
                rendezVous.setStatut(StatutRendezVous.PREVU);
            }

            rendezVousService.createRendezVous(rendezVous);
            redirectAttributes.addFlashAttribute("success", "Rendez-vous créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/rendezvous/nouveau";
        }

        return "redirect:/rendezvous";
    }

    /**
     * Affiche les détails d'un rendez-vous
     */
    @GetMapping("/{id}")
    public String viewRendezVous(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        RendezVous rendezVous = rendezVousService.getRendezVousById(id);
        if (rendezVous == null) {
            return "redirect:/rendezvous";
        }

        model.addAttribute("rendezVous", rendezVous);
        return "rendezvous/view";
    }

    /**
     * Valide un rendez-vous
     */
    @GetMapping("/valider/{id}")
    public String validerRendezVous(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        try {
            rendezVousService.validerRendezVous(id);
            redirectAttributes.addFlashAttribute("success", "Rendez-vous validé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return "redirect:/rendezvous";
    }

    /**
     * Affiche le formulaire d'annulation
     */
    @GetMapping("/annuler/{id}")
    public String showAnnulationForm(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        RendezVous rendezVous = rendezVousService.getRendezVousById(id);
        if (rendezVous == null) {
            return "redirect:/rendezvous";
        }

        model.addAttribute("rendezVous", rendezVous);
        return "rendezvous/annulation";
    }

    /**
     * Annule un rendez-vous
     */
    @PostMapping("/annuler/{id}")
    public String annulerRendezVous(@PathVariable Long id,
                                    @RequestParam String motif,
                                    RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        try {
            rendezVousService.annulerRendezVous(id, motif);
            redirectAttributes.addFlashAttribute("success", "Rendez-vous annulé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return "redirect:/rendezvous";
    }

    /**
     * Supprime définitivement un rendez-vous
     */
    @GetMapping("/delete/{id}")
    public String deleteRendezVous(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        try {
            rendezVousService.deleteRendezVous(id);
            redirectAttributes.addFlashAttribute("success", "Rendez-vous supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return "redirect:/rendezvous";
    }

    /**
     * Vérifie la disponibilité d'un créneau (API AJAX)
     */
    @GetMapping("/verifier-disponibilite")
    @ResponseBody
    public String verifierDisponibilite(@RequestParam Long medecinId,
                                        @RequestParam String date,
                                        @RequestParam String heure) {
        try {
            LocalDateTime dateHeure = LocalDateTime.parse(date + "T" + heure + ":00");
            boolean disponible = rendezVousService.isCreneauDisponible(medecinId, dateHeure, 30);

            if (disponible) {
                return "{\"disponible\": true, \"message\": \"Créneau disponible\"}";
            } else {
                return "{\"disponible\": false, \"message\": \"Médecin non disponible sur ce créneau\"}";
            }
        } catch (Exception e) {
            return "{\"disponible\": false, \"message\": \"Erreur de vérification\"}";
        }
    }

    /**
     * Rendez-vous du jour
     */
    @GetMapping("/aujourdhui")
    public String rendezVousAujourdhui(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        List<RendezVous> rdvs = rendezVousService.getRendezVousDuJour();
        model.addAttribute("rendezvous", rdvs);
        model.addAttribute("filter", "aujourdhui");

        return "rendezvous/list";
    }

    /**
     * Rendez-vous par médecin
     */
    @GetMapping("/medecin/{id}")
    public String rendezVousParMedecin(@PathVariable Long id,
                                       @RequestParam(required = false) String date,
                                       Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        LocalDate dateConsultation = date != null ?
                LocalDate.parse(date) : LocalDate.now();

        List<RendezVous> rdvs = rendezVousService.getRendezVousByMedecinAndDate(id, dateConsultation);
        model.addAttribute("rendezvous", rdvs);
        model.addAttribute("medecin", medecinService.getMedecinById(id));
        model.addAttribute("date", dateConsultation);

        return "rendezvous/agenda";
    }
}
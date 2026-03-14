package com.clinique.controller;

import com.clinique.entity.Admin;
import com.clinique.entity.Medecin;
import com.clinique.entity.Utilisateur;
import com.clinique.service.AuthService;
import com.clinique.service.ConsultationService;
import com.clinique.service.MedecinService;
import com.clinique.service.PatientService;
import com.clinique.service.RendezVousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class DashboardController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedecinService medecinService;

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private ConsultationService consultationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Vérifier si l'utilisateur est connecté
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        model.addAttribute("user", user);

        // Ajouter la date du jour
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        model.addAttribute("dateToday", today.format(formatter));

        // Redirection selon le rôle
        if (user instanceof Admin) {
            return adminDashboard(model);
        } else if (user instanceof Medecin) {
            // Rediriger vers l'espace médecin
            return "redirect:/mon-espace/dashboard";
        } else {
            return "redirect:/login";
        }
    }

    private String adminDashboard(Model model) {
        model.addAttribute("totalPatients", patientService.countPatients());
        model.addAttribute("totalMedecins", medecinService.countMedecinsActifs());
        model.addAttribute("totalRendezVous", rendezVousService.countRendezVous());
        model.addAttribute("totalConsultations", consultationService.countConsultations());
        model.addAttribute("derniersPatients", patientService.getDerniersPatients(5));
        model.addAttribute("rdvDuJour", rendezVousService.getRendezVousDuJour());
        model.addAttribute("medecinsActifs", medecinService.getMedecinsActifs());
        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (user instanceof Admin) {
            return "redirect:/dashboard";
        } else if (user instanceof Medecin) {
            return "redirect:/mon-espace/dashboard";
        } else {
            return "redirect:/login";
        }
    }
}
package com.clinique.controller;

import com.clinique.entity.Consultation;
import com.clinique.entity.Utilisateur;
import com.clinique.service.AuthService;
import com.clinique.service.ConsultationService;
import com.clinique.service.RendezVousService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public String listConsultations(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();

        if ("MEDECIN".equals(user.getRole().name())) {
            model.addAttribute("consultations",
                    consultationService.getConsultationsByMedecin(user.getId()));
        } else {
            model.addAttribute("consultations", consultationService.getAllConsultations());
        }

        return "consultations/list";
    }

    @GetMapping("/patient/{patientId}")
    public String consultationsByPatient(@PathVariable Long patientId, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("consultations",
                consultationService.getConsultationsByPatient(patientId));
        return "consultations/list";
    }

    @GetMapping("/nouvelle")
    public String showForm(@RequestParam(required = false) Long rdvId, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Consultation consultation;

        if (rdvId != null) {
            // Créer à partir d'un rendez-vous
            consultation = consultationService.createConsultationFromRendezVous(rdvId);
        } else {
            consultation = new Consultation();
        }

        model.addAttribute("consultation", consultation);
        return "consultations/form";
    }

    @PostMapping("/save")
    public String saveConsultation(@Valid @ModelAttribute Consultation consultation,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "consultations/form";
        }

        try {
            if (consultation.getId() == null) {
                consultationService.createConsultationDirecte(consultation);
                redirectAttributes.addFlashAttribute("success", "Consultation créée avec succès");
            } else {
                consultationService.updateConsultation(consultation);
                redirectAttributes.addFlashAttribute("success", "Consultation mise à jour avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/consultations/nouvelle";
        }

        return "redirect:/consultations";
    }

    @GetMapping("/{id}")
    public String viewConsultation(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Consultation consultation = consultationService.getConsultationById(id);
        if (consultation == null) {
            return "redirect:/consultations";
        }

        model.addAttribute("consultation", consultation);
        return "consultations/view";
    }

    @GetMapping("/edit/{id}")
    public String editConsultation(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Consultation consultation = consultationService.getConsultationById(id);
        if (consultation == null) {
            return "redirect:/consultations";
        }

        model.addAttribute("consultation", consultation);
        return "consultations/form";
    }

    @GetMapping("/terminer/{id}")
    public String terminerConsultation(@PathVariable Long id,
                                       RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            consultationService.terminerConsultation(id);
            redirectAttributes.addFlashAttribute("success", "Consultation terminée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return "redirect:/consultations";
    }

    @GetMapping("/delete/{id}")
    public String deleteConsultation(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            consultationService.deleteConsultation(id);
            redirectAttributes.addFlashAttribute("success", "Consultation supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return "redirect:/consultations";
    }
}
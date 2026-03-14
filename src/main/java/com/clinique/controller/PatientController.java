package com.clinique.controller;

import com.clinique.entity.Patient;
import com.clinique.entity.Utilisateur;
import com.clinique.service.AuthService;
import com.clinique.service.PatientService;
import com.clinique.service.MedecinService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedecinService medecinService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public String listPatients(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();

        if ("MEDECIN".equals(user.getRole().name())) {
            model.addAttribute("patients", patientService.getPatientsByMedecin(user.getId()));
        } else {
            model.addAttribute("patients", patientService.getAllPatients());
        }

        return "patients/list";
    }

    @GetMapping("/nouveau")
    public String showForm(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("patient", new Patient());
        model.addAttribute("medecins", medecinService.getMedecinsActifs());
        return "patients/form";
    }

    @GetMapping("/edit/{id}")
    public String editPatient(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        model.addAttribute("medecins", medecinService.getMedecinsActifs());
        return "patients/form";
    }

    @PostMapping("/save")
    public String savePatient(@Valid @ModelAttribute Patient patient,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        if (result.hasErrors()) {
            model.addAttribute("medecins", medecinService.getMedecinsActifs());
            return "patients/form";
        }

        try {
            // S'assurer que le médecin traitant est un objet complet (et pas seulement un id)
            if (patient.getMedecinTraitant() != null &&
                    patient.getMedecinTraitant().getId() != null) {
                patient.setMedecinTraitant(
                        medecinService.getMedecinById(patient.getMedecinTraitant().getId())
                );
            }

            if (patient.getId() == null) {
                Patient savedPatient = patientService.createPatient(patient);

                String message = "Patient créé avec succès";
                if (savedPatient.getMedecinTraitant() != null) {
                    message += " et associé au Dr. " +
                            savedPatient.getMedecinTraitant().getPrenom() + " " +
                            savedPatient.getMedecinTraitant().getNom();
                }

                redirectAttributes.addFlashAttribute("success", message);
            } else {
                patientService.updatePatient(patient);
                redirectAttributes.addFlashAttribute("success", "Patient modifié avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/patients/nouveau";
        }

        return "redirect:/patients";
    }

    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Patient patient = patientService.getPatientById(id);

        Utilisateur user = authService.getCurrentUser();
        if ("MEDECIN".equals(user.getRole().name()) &&
                !patient.getMedecinTraitant().getId().equals(user.getId())) {
            return "redirect:/patients";
        }

        model.addAttribute("patient", patient);
        return "patients/view";
    }
}
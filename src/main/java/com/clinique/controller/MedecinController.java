package com.clinique.controller;

import com.clinique.entity.Consultation;
import com.clinique.entity.Medecin;
import com.clinique.entity.Specialite;
import com.clinique.entity.Utilisateur;
import com.clinique.service.AuthService;
import com.clinique.service.ConsultationService;
import com.clinique.service.MedecinService;
import com.clinique.service.SpecialiteService;
import com.clinique.service.RendezVousService;
import com.clinique.entity.RendezVous;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.time.LocalDate;

@Controller
@RequestMapping("/medecins")
public class MedecinController {

    @Autowired
    private MedecinService medecinService;

    @Autowired
    private SpecialiteService specialiteService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private RendezVousService rendezVousService;

    @GetMapping
    public String listMedecins(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        // Seul l'admin peut voir tous les médecins
        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("medecins", medecinService.getAllMedecins());
        return "medecins/list";
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

        model.addAttribute("medecin", new Medecin());
        model.addAttribute("specialites", specialiteService.getAllSpecialites());
        return "medecins/form";
    }

    @PostMapping("/save")
    public String saveMedecin(@Valid @ModelAttribute Medecin medecin,
                              BindingResult result,
                              @RequestParam(required = false) String action,
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
            model.addAttribute("specialites", specialiteService.getAllSpecialites());
            return "medecins/form";
        }

        try {
            if (medecin.getId() == null) {
                medecinService.createMedecin(medecin);
                redirectAttributes.addFlashAttribute("success", "Médecin créé avec succès");
            } else {
                medecinService.updateMedecin(medecin);
                redirectAttributes.addFlashAttribute("success", "Médecin modifié avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/medecins/nouveau";
        }

        return "redirect:/medecins";
    }

    @GetMapping("/edit/{id}")
    public String editMedecin(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        Medecin medecin = medecinService.getMedecinById(id);
        if (medecin == null) {
            return "redirect:/medecins";
        }

        model.addAttribute("medecin", medecin);
        model.addAttribute("specialites", specialiteService.getAllSpecialites());
        return "medecins/form";
    }

    @GetMapping("/{id}")
    public String viewMedecin(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Medecin medecin = medecinService.getMedecinById(id);
        if (medecin == null) {
            return "redirect:/medecins";
        }

        // Prochains rendez-vous de ce médecin (liste compacte)
        List<RendezVous> prochainsRdv = rendezVousService.getProchainsRendezVousMedecin(id, 10);

        // Rendez-vous du jour (agenda journalier)
        LocalDate aujourdHui = LocalDate.now();
        List<RendezVous> rdvJour = rendezVousService.getRendezVousByMedecinAndDate(id, aujourdHui);

        model.addAttribute("medecin", medecin);
        model.addAttribute("prochainsRendezVous", prochainsRdv);
        model.addAttribute("agendaDate", aujourdHui);
        model.addAttribute("agendaRendezVous", rdvJour);
        return "medecins/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteMedecin(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        try {
            medecinService.deleteMedecin(id);
            redirectAttributes.addFlashAttribute("success", "Médecin désactivé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return "redirect:/medecins";
    }

    /**
     * Liste des consultations du médecin
     */
    @GetMapping("/consultations")
    public String mesConsultations(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (user == null || !(user instanceof Medecin)) {
            return "redirect:/consultations";
        }
        Medecin medecin = (Medecin) user;

        List<Consultation> consultations = consultationService.getConsultationsByMedecin(medecin.getId());

        model.addAttribute("consultations", consultations);
        model.addAttribute("medecin", medecin);

        return "medecins/consultations";
    }
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("medecins", medecinService.searchMedecins(keyword));
        return "medecins/list";
    }
}
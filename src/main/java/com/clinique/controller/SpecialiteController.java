package com.clinique.controller;

import com.clinique.entity.Specialite;
import com.clinique.entity.Utilisateur;
import com.clinique.service.AuthService;
import com.clinique.service.SpecialiteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/specialites")
public class SpecialiteController {

    @Autowired
    private SpecialiteService specialiteService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public String listSpecialites(Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("specialites", specialiteService.getAllSpecialites());
        return "specialites/list";
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

        model.addAttribute("specialite", new Specialite());
        return "specialites/form";
    }

    @PostMapping("/save")
    public String saveSpecialite(@Valid @ModelAttribute Specialite specialite,
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
            return "specialites/form";
        }

        try {
            if (specialite.getId() == null) {
                specialiteService.createSpecialite(specialite);
                redirectAttributes.addFlashAttribute("success", "Spécialité créée avec succès");
            } else {
                specialiteService.updateSpecialite(specialite);
                redirectAttributes.addFlashAttribute("success", "Spécialité modifiée avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/specialites/nouveau";
        }

        return "redirect:/specialites";
    }

    @GetMapping("/edit/{id}")
    public String editSpecialite(@PathVariable Long id, Model model) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        Specialite specialite = specialiteService.getSpecialiteById(id);
        if (specialite == null) {
            return "redirect:/specialites";
        }

        model.addAttribute("specialite", specialite);
        return "specialites/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSpecialite(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated()) {
            return "redirect:/login";
        }

        Utilisateur user = authService.getCurrentUser();
        if (!"ADMIN".equals(user.getRole().name())) {
            return "redirect:/dashboard";
        }

        try {
            specialiteService.deleteSpecialite(id);
            redirectAttributes.addFlashAttribute("success", "Spécialité supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return "redirect:/specialites";
    }
}
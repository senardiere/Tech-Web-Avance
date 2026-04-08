package com.clinique.endpoint;

import com.clinique.entity.Specialite;
import com.clinique.service.SpecialiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/specialites")
public class SpecialiteEndpoint {

    @Autowired
    private SpecialiteService specialiteService;

    // Conversion manuelle pour éviter la boucle infinie
    private Map<String, Object> convertToMap(Specialite s) {
        Map<String, Object> map = new HashMap<>();
        if (s == null) return map;

        map.put("id", s.getId());
        map.put("nom", s.getNom());
        map.put("code", s.getCode());
        map.put("description", s.getDescription());
        map.put("actif", s.isActif());

        return map;
    }

    // ========== GET ==========

    @GetMapping
    public List<Map<String, Object>> getAllSpecialites() {
        return specialiteService.getAllSpecialites().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/actives")
    public List<Map<String, Object>> getSpecialitesActives() {
        return specialiteService.getSpecialitesActives().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getSpecialiteById(@PathVariable Long id) {
        Specialite specialite = specialiteService.getSpecialiteById(id);
        return convertToMap(specialite);
    }

    @GetMapping("/nom/{nom}")
    public Map<String, Object> getSpecialiteByNom(@PathVariable String nom) {
        Specialite specialite = specialiteService.getSpecialiteByNom(nom);
        return convertToMap(specialite);
    }

    @GetMapping("/code/{code}")
    public Map<String, Object> getSpecialiteByCode(@PathVariable String code) {
        Specialite specialite = specialiteService.getSpecialiteByCode(code);
        return convertToMap(specialite);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchSpecialites(@RequestParam String keyword) {
        return specialiteService.searchSpecialites(keyword).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/stats")
    public Object getSpecialiteStats(@PathVariable Long id) {
        return specialiteService.getSpecialiteStats(id);
    }

    @GetMapping("/{id}/medecins")
    public List<Object[]> getMedecinsBySpecialite(@PathVariable Long id) {
        return specialiteService.getMedecinsBySpecialite(id);
    }

    // ========== POST ==========

    @PostMapping
    public Specialite createSpecialite(@RequestBody Specialite specialite) {
        return specialiteService.createSpecialite(specialite);
    }

    // ========== PUT ==========

    @PutMapping("/{id}")
    public Specialite updateSpecialite(@PathVariable Long id, @RequestBody Specialite specialite) {
        specialite.setId(id);
        return specialiteService.updateSpecialite(specialite);
    }

    // ========== PATCH ==========

    @PatchMapping("/{id}/activer")
    public void activerSpecialite(@PathVariable Long id) {
        specialiteService.activerSpecialite(id);
    }

    @PatchMapping("/{id}/desactiver")
    public void desactiverSpecialite(@PathVariable Long id) {
        specialiteService.desactiverSpecialite(id);
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    public void deleteSpecialite(@PathVariable Long id) {
        specialiteService.deleteSpecialite(id);
    }

    // ========== STATISTIQUES ==========

    @GetMapping("/stats/count")
    public long countSpecialites() {
        return specialiteService.countSpecialites();
    }

    @GetMapping("/stats/count-actives")
    public long countSpecialitesActives() {
        return specialiteService.countSpecialitesActives();
    }
}
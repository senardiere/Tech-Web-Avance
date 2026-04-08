package com.clinique.endpoint;

import com.clinique.entity.Patient;
import com.clinique.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/patients")
public class PatientEndpoint {

    @Autowired
    private PatientService patientService;

    // Conversion manuelle pour éviter la boucle infinie
    private Map<String, Object> convertToMap(Patient p) {
        Map<String, Object> map = new HashMap<>();
        if (p == null) return map;

        map.put("id", p.getId());
        map.put("nom", p.getNom());
        map.put("prenom", p.getPrenom());
        map.put("email", p.getEmail());
        map.put("telephone", p.getTelephone());
        map.put("adresse", p.getAdresse());
        map.put("dateNaissance", p.getDateNaissance());
        map.put("numeroSecuriteSociale", p.getNumeroSecuriteSociale());
        map.put("mutuelle", p.getMutuelle());
        map.put("personneContact", p.getPersonneContact());
        map.put("telephoneContact", p.getTelephoneContact());
        map.put("statut", p.getStatut() != null ? p.getStatut().name() : null);
        map.put("dateCreation", p.getDateCreation());
        map.put("derniereVisite", p.getDerniereVisite());

        if (p.getMedecinTraitant() != null) {
            Map<String, Object> medecin = new HashMap<>();
            medecin.put("id", p.getMedecinTraitant().getId());
            medecin.put("nom", p.getMedecinTraitant().getNom());
            medecin.put("prenom", p.getMedecinTraitant().getPrenom());
            map.put("medecinTraitant", medecin);
        }

        return map;
    }

    // ========== GET ==========

    @GetMapping
    public List<Map<String, Object>> getAllPatients() {
        return patientService.getAllPatients().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/actifs")
    public List<Map<String, Object>> getAllPatientsActifs() {
        return patientService.getAllPatientsActifs().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return convertToMap(patient);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchPatients(@RequestParam String nom) {
        return patientService.rechercherParNom(nom).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/derniers")
    public List<Map<String, Object>> getDerniersPatients(@RequestParam(defaultValue = "10") int limit) {
        return patientService.getDerniersPatients(limit).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}")
    public List<Map<String, Object>> getPatientsByMedecin(@PathVariable Long medecinId) {
        return patientService.getPatientsByMedecin(medecinId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}/derniers")
    public List<Map<String, Object>> getDerniersPatientsByMedecin(
            @PathVariable Long medecinId,
            @RequestParam(defaultValue = "10") int limit) {
        return patientService.getDerniersPatientsByMedecin(medecinId, limit).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}/search")
    public List<Map<String, Object>> searchPatientsByMedecin(
            @RequestParam String keyword,
            @PathVariable Long medecinId) {
        return patientService.searchPatientsByMedecin(keyword, medecinId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    // ========== POST ==========

    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    // ========== PUT ==========

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        patient.setId(id);
        return patientService.updatePatient(patient);
    }

    @PutMapping("/{id}/derniere-visite")
    public void updateDerniereVisite(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateVisite) {
        patientService.updateDerniereVisite(id, dateVisite);
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }

    // ========== STATISTIQUES ==========

    @GetMapping("/stats/count")
    public long countPatients() {
        return patientService.countPatients();
    }

    @GetMapping("/stats/count-by-medecin/{medecinId}")
    public long countPatientsByMedecin(@PathVariable Long medecinId) {
        return patientService.countPatientsByMedecin(medecinId);
    }

    @GetMapping("/stats/count-by-medecin-date")
    public long countPatientsByMedecinAndDateBetween(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return patientService.countPatientsByMedecinAndDateBetween(medecinId, debut, fin);
    }
}
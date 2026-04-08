package com.clinique.endpoint;

import com.clinique.entity.Consultation;
import com.clinique.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/consultations")
public class ConsultationEndpoint {

    @Autowired
    private ConsultationService consultationService;

    private Map<String, Object> convertToMap(Consultation c) {
        Map<String, Object> map = new HashMap<>();
        if (c == null) return map;

        map.put("id", c.getId());
        map.put("dateConsultation", c.getDateConsultation());
        map.put("statut", c.getStatut() != null ? c.getStatut().name() : null);
        map.put("diagnostic", c.getDiagnostic());
        map.put("prescriptions", c.getPrescriptions());
        map.put("observations", c.getObservations());
        map.put("poids", c.getPoids());
        map.put("taille", c.getTaille());
        map.put("tension", c.getTension());
        map.put("prochainRdv", c.getProchainRdv());

        if (c.getPatient() != null) {
            Map<String, Object> patient = new HashMap<>();
            patient.put("id", c.getPatient().getId());
            patient.put("nom", c.getPatient().getNom());
            patient.put("prenom", c.getPatient().getPrenom());
            map.put("patient", patient);
        }

        if (c.getMedecin() != null) {
            Map<String, Object> medecin = new HashMap<>();
            medecin.put("id", c.getMedecin().getId());
            medecin.put("nom", c.getMedecin().getNom());
            medecin.put("prenom", c.getMedecin().getPrenom());
            map.put("medecin", medecin);
        }

        if (c.getRendezVous() != null) {
            Map<String, Object> rdv = new HashMap<>();
            rdv.put("id", c.getRendezVous().getId());
            map.put("rendezVous", rdv);
        }

        return map;
    }

    @GetMapping
    public List<Map<String, Object>> getAllConsultations() {
        return consultationService.getAllConsultations().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getConsultationById(@PathVariable Long id) {
        Consultation consultation = consultationService.getConsultationById(id);
        return convertToMap(consultation);
    }

    @GetMapping("/patient/{patientId}")
    public List<Map<String, Object>> getConsultationsByPatient(@PathVariable Long patientId) {
        return consultationService.getConsultationsByPatient(patientId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}")
    public List<Map<String, Object>> getConsultationsByMedecin(@PathVariable Long medecinId) {
        return consultationService.getConsultationsByMedecin(medecinId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}/dernieres")
    public List<Map<String, Object>> getDernieresConsultationsByMedecin(
            @PathVariable Long medecinId,
            @RequestParam(defaultValue = "10") int limit) {
        return consultationService.getDernieresConsultationsByMedecin(medecinId, limit).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/patient/{patientId}/medecin/{medecinId}")
    public List<Map<String, Object>> getConsultationsByPatientAndMedecin(
            @PathVariable Long patientId,
            @PathVariable Long medecinId) {
        return consultationService.getConsultationsByPatientAndMedecin(patientId, medecinId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @PostMapping
    public Consultation createConsultation(@RequestBody Consultation consultation) {
        return consultationService.createConsultation(consultation);
    }

    @PostMapping("/from-rendezvous/{rendezVousId}")
    public Consultation createConsultationFromRendezVous(@PathVariable Long rendezVousId) {
        return consultationService.createConsultationFromRendezVous(rendezVousId);
    }

    @PostMapping("/directe")
    public Consultation createConsultationDirecte(@RequestBody Consultation consultation) {
        return consultationService.createConsultationDirecte(consultation);
    }

    @PatchMapping("/{id}/terminer")
    public void terminerConsultation(@PathVariable Long id) {
        consultationService.terminerConsultation(id);
    }

    @PutMapping("/{id}")
    public Consultation updateConsultation(@PathVariable Long id, @RequestBody Consultation consultation) {
        consultation.setId(id);
        return consultationService.updateConsultation(consultation);
    }

    @DeleteMapping("/{id}")
    public void deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
    }

    @GetMapping("/stats/total")
    public long countConsultations() {
        return consultationService.countConsultations();
    }

    @GetMapping("/stats/medecin/{medecinId}")
    public long countConsultationsByMedecin(@PathVariable Long medecinId) {
        return consultationService.countConsultationsByMedecin(medecinId);
    }

    @GetMapping("/stats/medecin/{medecinId}/periode")
    public long countConsultationsByMedecinAndDateBetween(
            @PathVariable Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return consultationService.countConsultationsByMedecinAndDateBetween(medecinId, debut, fin);
    }
}
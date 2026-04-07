package com.clinique.endpoint;

import com.clinique.entity.Consultation;
import com.clinique.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/internal/consultations")
public class ConsultationEndpoint {

    @Autowired
    private ConsultationService consultationService;


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



    // RECHERCHES
    @GetMapping
    public List<Consultation> getAllConsultations() {
        return consultationService.getAllConsultations();
    }


    @GetMapping("/{id}")
    public Consultation getConsultationById(@PathVariable Long id) {
        return consultationService.getConsultationById(id);
    }


    @GetMapping("/patient/{patientId}")
    public List<Consultation> getConsultationsByPatient(@PathVariable Long patientId) {
        return consultationService.getConsultationsByPatient(patientId);
    }


    @GetMapping("/medecin/{medecinId}")
    public List<Consultation> getConsultationsByMedecin(@PathVariable Long medecinId) {
        return consultationService.getConsultationsByMedecin(medecinId);
    }


    @GetMapping("/medecin/{medecinId}/dernieres")
    public List<Consultation> getDernieresConsultationsByMedecin(
            @PathVariable Long medecinId,
            @RequestParam(defaultValue = "10") int limit) {
        return consultationService.getDernieresConsultationsByMedecin(medecinId, limit);
    }


    @GetMapping("/patient/{patientId}/medecin/{medecinId}")
    public List<Consultation> getConsultationsByPatientAndMedecin(
            @PathVariable Long patientId,
            @PathVariable Long medecinId) {
        return consultationService.getConsultationsByPatientAndMedecin(patientId, medecinId);
    }

    // STATISTIQUES

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
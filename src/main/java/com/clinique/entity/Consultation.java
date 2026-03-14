package com.clinique.entity;

import com.clinique.enums.StatutConsultation;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "rendez_vous_id", unique = true)
    private RendezVous rendezVous;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @Column(length = 2000)
    private String diagnostic;

    @Column(length = 2000)
    private String prescriptions;

    @Column(length = 2000)
    private String observations;

    private LocalDateTime dateConsultation;
    private LocalDateTime dateCloture;

    @Enumerated(EnumType.STRING)
    private StatutConsultation statut = StatutConsultation.EN_COURS;

    private Double poids;
    private Double taille;
    private String tension;
    private LocalDateTime prochainRdv;

    public Consultation() {
        this.dateConsultation = LocalDateTime.now();
    }

    public void terminer() {
        this.statut = StatutConsultation.TERMINEE;
        this.dateCloture = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RendezVous getRendezVous() { return rendezVous; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getPrescriptions() { return prescriptions; }
    public void setPrescriptions(String prescriptions) { this.prescriptions = prescriptions; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public LocalDateTime getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(LocalDateTime dateConsultation) { this.dateConsultation = dateConsultation; }

    public LocalDateTime getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDateTime dateCloture) { this.dateCloture = dateCloture; }

    public StatutConsultation getStatut() { return statut; }
    public void setStatut(StatutConsultation statut) { this.statut = statut; }

    public Double getPoids() { return poids; }
    public void setPoids(Double poids) { this.poids = poids; }

    public Double getTaille() { return taille; }
    public void setTaille(Double taille) { this.taille = taille; }

    public String getTension() { return tension; }
    public void setTension(String tension) { this.tension = tension; }

    public LocalDateTime getProchainRdv() { return prochainRdv; }
    public void setProchainRdv(LocalDateTime prochainRdv) { this.prochainRdv = prochainRdv; }
}
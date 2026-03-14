package com.clinique.entity;

import com.clinique.enums.StatutRendezVous;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private Integer duree = 30;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20, nullable = false)
    private StatutRendezVous statut = StatutRendezVous.PREVU;

    private String motif;
    private String motifAnnulation;
    private LocalDateTime datePrise;
    private LocalDateTime dateValidation;
    private LocalDateTime dateAnnulation;

    @OneToOne(mappedBy = "rendezVous")
    private Consultation consultation;

    public RendezVous() {
        this.datePrise = LocalDateTime.now();
    }

    public void valider() {
        this.statut = StatutRendezVous.VALIDE;
        this.dateValidation = LocalDateTime.now();
    }

    public void annuler(String motif) {
        this.statut = StatutRendezVous.ANNULE;
        this.motifAnnulation = motif;
        this.dateAnnulation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public StatutRendezVous getStatut() { return statut; }
    public void setStatut(StatutRendezVous statut) { this.statut = statut; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }

    public LocalDateTime getDatePrise() { return datePrise; }
    public void setDatePrise(LocalDateTime datePrise) { this.datePrise = datePrise; }

    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }

    public LocalDateTime getDateAnnulation() { return dateAnnulation; }
    public void setDateAnnulation(LocalDateTime dateAnnulation) { this.dateAnnulation = dateAnnulation; }

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
}
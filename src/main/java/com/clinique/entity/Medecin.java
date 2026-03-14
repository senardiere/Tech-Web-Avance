package com.clinique.entity;

import com.clinique.enums.Role;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Medecin")  // ← Changé de "MEDECIN" à "Medecin"
public class Medecin extends Utilisateur {

    @Column(name = "numero_licence", unique = true)
    private String numeroLicence;

    @ManyToOne
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @Column(name = "cabinet")
    private String cabinet;

    @ElementCollection
    @CollectionTable(name = "medecin_disponibilites",
            joinColumns = @JoinColumn(name = "medecin_id"))
    @Column(name = "jour_disponible")
    private List<String> joursDisponibles = new ArrayList<>();

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL)
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "medecin")
    private List<Consultation> consultations = new ArrayList<>();

    public Medecin() {
        super();
        this.setRole(Role.MEDECIN);
    }

    public Medecin(String nom, String prenom, String email, String login, String motDePasse) {
        super(nom, prenom, email, login, motDePasse, Role.MEDECIN);
        this.joursDisponibles = new ArrayList<>();
    }

    // Getters et Setters
    public String getNumeroLicence() {
        return numeroLicence;
    }

    public void setNumeroLicence(String numeroLicence) {
        this.numeroLicence = numeroLicence;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public String getCabinet() {
        return cabinet;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public List<String> getJoursDisponibles() {
        return joursDisponibles;
    }

    public void setJoursDisponibles(List<String> joursDisponibles) {
        this.joursDisponibles = joursDisponibles;
    }

    public List<RendezVous> getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(List<RendezVous> rendezVous) {
        this.rendezVous = rendezVous;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    // Méthode utilitaire pour ajouter un jour de disponibilité
    public void addJourDisponible(String jour) {
        if (this.joursDisponibles == null) {
            this.joursDisponibles = new ArrayList<>();
        }
        if (!this.joursDisponibles.contains(jour)) {
            this.joursDisponibles.add(jour);
        }
    }

    // Méthode utilitaire pour retirer un jour de disponibilité
    public void removeJourDisponible(String jour) {
        if (this.joursDisponibles != null) {
            this.joursDisponibles.remove(jour);
        }
    }
}
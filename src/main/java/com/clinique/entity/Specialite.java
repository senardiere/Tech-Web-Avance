package com.clinique.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "specialite")
public class Specialite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, unique = true)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "actif")
    private boolean actif = true;

    @OneToMany(mappedBy = "specialite")
    private List<Medecin> medecins = new ArrayList<>();

    // Constructeurs
    public Specialite() {}

    public Specialite(String nom, String description) {
        this.nom = nom;
        this.description = description;
        this.actif = true;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public List<Medecin> getMedecins() {
        return medecins;
    }

    public void setMedecins(List<Medecin> medecins) {
        this.medecins = medecins;
    }

    // Méthodes utilitaires
    @Override
    public String toString() {
        return "Specialite{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", code='" + code + '\'' +
                ", actif=" + actif +
                '}';
    }
}
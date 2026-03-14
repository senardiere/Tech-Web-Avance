package com.clinique.entity;

import com.clinique.enums.Role;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Admin")  // ← Changé de "ADMIN" à "Admin"
public class Admin extends Utilisateur {

    private String niveauAcces;
    private String departement;

    public Admin() {
        super();
        this.setRole(Role.ADMIN);
        this.niveauAcces = "TOTAL";
    }

    public Admin(String nom, String prenom, String email, String login, String motDePasse) {
        super(nom, prenom, email, login, motDePasse, Role.ADMIN);
        this.niveauAcces = "TOTAL";
    }

    // Getters et Setters
    public String getNiveauAcces() {
        return niveauAcces;
    }

    public void setNiveauAcces(String niveauAcces) {
        this.niveauAcces = niveauAcces;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }
}
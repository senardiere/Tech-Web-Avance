package com.clinique.entity;

import com.clinique.enums.StatutPatient;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "last_name")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(name = "first_name")
    private String prenom;

    private LocalDate dateNaissance;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(unique = true)
    private String email;

    private String telephone;
    private String adresse;

    @Column(name = "numero_securite_sociale", unique = true)
    private String numeroSecuriteSociale;

    private String mutuelle;
    private String personneContact;
    private String telephoneContact;

    @Enumerated(EnumType.STRING)
    private StatutPatient statut = StatutPatient.ACTIF;  // ← Plus d'erreur

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "derniere_visite")
    private LocalDate derniereVisite;

    @ManyToOne
    @JoinColumn(name = "medecin_traitant_id")
    private Medecin medecinTraitant;

    // Constructeurs
    public Patient() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) { this.numeroSecuriteSociale = numeroSecuriteSociale; }

    public String getMutuelle() { return mutuelle; }
    public void setMutuelle(String mutuelle) { this.mutuelle = mutuelle; }

    public String getPersonneContact() { return personneContact; }
    public void setPersonneContact(String personneContact) { this.personneContact = personneContact; }

    public String getTelephoneContact() { return telephoneContact; }
    public void setTelephoneContact(String telephoneContact) { this.telephoneContact = telephoneContact; }

    public StatutPatient getStatut() { return statut; }
    public void setStatut(StatutPatient statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDate getDerniereVisite() { return derniereVisite; }
    public void setDerniereVisite(LocalDate derniereVisite) { this.derniereVisite = derniereVisite; }

    public Medecin getMedecinTraitant() { return medecinTraitant; }
    public void setMedecinTraitant(Medecin medecinTraitant) { this.medecinTraitant = medecinTraitant; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
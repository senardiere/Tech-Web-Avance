package com.clinique.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String utilisateur;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entite;

    private Long entiteId;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private LocalDateTime dateAction;

    private String adresseIp;

    public AuditLog() {
        this.dateAction = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntite() { return entite; }
    public void setEntite(String entite) { this.entite = entite; }

    public Long getEntiteId() { return entiteId; }
    public void setEntiteId(Long entiteId) { this.entiteId = entiteId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getDateAction() { return dateAction; }
    public void setDateAction(LocalDateTime dateAction) { this.dateAction = dateAction; }

    public String getAdresseIp() { return adresseIp; }
    public void setAdresseIp(String adresseIp) { this.adresseIp = adresseIp; }
}
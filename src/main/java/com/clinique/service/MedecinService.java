package com.clinique.service;

import com.clinique.dao.MedecinDAO;
import com.clinique.dao.SpecialiteDAO;
import com.clinique.entity.Medecin;
import com.clinique.entity.Specialite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MedecinService {

    @Autowired
    private MedecinDAO medecinDAO;

    @Autowired
    private SpecialiteDAO specialiteDAO;

    @Autowired
    private AuditService auditService;

    /**
     * Créer un nouveau médecin
     */
    public Medecin createMedecin(Medecin medecin) {
        // Vérifications métier
        if (medecinDAO.existsByEmail(medecin.getEmail())) {
            throw new RuntimeException("Un médecin avec cet email existe déjà");
        }
        if (medecinDAO.existsByLogin(medecin.getLogin())) {
            throw new RuntimeException("Un médecin avec ce login existe déjà");
        }
        if (medecinDAO.existsByNumeroLicence(medecin.getNumeroLicence())) {
            throw new RuntimeException("Un médecin avec ce numéro de licence existe déjà");
        }

        Medecin savedMedecin = medecinDAO.save(medecin);
        auditService.log("CREATE", "Medecin", savedMedecin.getId(), "Création médecin: " + savedMedecin.getNomComplet());
        return savedMedecin;
    }

    /**
     * Mettre à jour un médecin
     */
    public Medecin updateMedecin(Medecin medecin) {
        Medecin existingMedecin = medecinDAO.findById(medecin.getId());
        if (existingMedecin == null) {
            throw new RuntimeException("Médecin non trouvé avec l'ID: " + medecin.getId());
        }

        // Vérifier l'unicité de l'email si changé
        if (!existingMedecin.getEmail().equals(medecin.getEmail()) &&
                medecinDAO.existsByEmail(medecin.getEmail())) {
            throw new RuntimeException("Un médecin avec cet email existe déjà");
        }

        // Vérifier l'unicité du login si changé
        if (!existingMedecin.getLogin().equals(medecin.getLogin()) &&
                medecinDAO.existsByLogin(medecin.getLogin())) {
            throw new RuntimeException("Un médecin avec ce login existe déjà");
        }

        // Vérifier l'unicité du numéro de licence si changé
        if (!existingMedecin.getNumeroLicence().equals(medecin.getNumeroLicence()) &&
                medecinDAO.existsByNumeroLicence(medecin.getNumeroLicence())) {
            throw new RuntimeException("Un médecin avec ce numéro de licence existe déjà");
        }

        Medecin updatedMedecin = medecinDAO.save(medecin);
        auditService.log("UPDATE", "Medecin", updatedMedecin.getId(), "Mise à jour médecin: " + updatedMedecin.getNomComplet());
        return updatedMedecin;
    }

    /**
     * Trouver un médecin par son ID
     */
    public Medecin getMedecinById(Long id) {
        Medecin medecin = medecinDAO.findById(id);
        if (medecin == null) {
            throw new RuntimeException("Médecin non trouvé avec l'ID: " + id);
        }
        return medecin;
    }

    /**
     * Trouver un médecin par son login
     */
    public Medecin getMedecinByLogin(String login) {
        return medecinDAO.findByLogin(login);
    }

    /**
     * Trouver un médecin par son email
     */
    public Medecin getMedecinByEmail(String email) {
        return medecinDAO.findByEmail(email);
    }

    /**
     * Trouver un médecin par son numéro de licence
     */
    public Medecin getMedecinByNumeroLicence(String numeroLicence) {
        return medecinDAO.findByNumeroLicence(numeroLicence);
    }

    /**
     * Trouver tous les médecins
     */
    public List<Medecin> getAllMedecins() {
        return medecinDAO.findAll();
    }

    /**
     * Trouver tous les médecins actifs
     */
    public List<Medecin> getMedecinsActifs() {
        return medecinDAO.findAllActifs();
    }

    /**
     * Trouver les médecins par spécialité
     */
    public List<Medecin> getMedecinsBySpecialite(Long specialiteId) {
        Specialite specialite = specialiteDAO.findById(specialiteId);
        if (specialite == null) {
            throw new RuntimeException("Spécialité non trouvée avec l'ID: " + specialiteId);
        }
        return medecinDAO.findBySpecialite(specialite);
    }

    /**
     * Trouver les médecins par spécialité (version avec objet Specialite)
     */
    public List<Medecin> getMedecinsBySpecialite(Specialite specialite) {
        if (specialite == null || specialite.getId() == null) {
            throw new RuntimeException("Spécialité invalide");
        }
        return medecinDAO.findBySpecialite(specialite);
    }

    /**
     * Compter le nombre total de médecins
     */
    public long countMedecins() {
        return medecinDAO.count();
    }

    /**
     * Compter le nombre de médecins actifs
     */
    public long countMedecinsActifs() {
        return medecinDAO.countActifs();
    }

    /**
     * Compter le nombre de médecins par spécialité
     */
    public long countMedecinsBySpecialite(Long specialiteId) {
        Specialite specialite = specialiteDAO.findById(specialiteId);
        if (specialite == null) {
            throw new RuntimeException("Spécialité non trouvée avec l'ID: " + specialiteId);
        }
        return medecinDAO.countBySpecialite(specialite);
    }

    /**
     * Compter le nombre de médecins par spécialité (version avec objet)
     */
    public long countMedecinsBySpecialite(Specialite specialite) {
        if (specialite == null || specialite.getId() == null) {
            throw new RuntimeException("Spécialité invalide");
        }
        return medecinDAO.countBySpecialite(specialite);
    }

    /**
     * Désactiver un médecin (soft delete)
     */
    public void desactiverMedecin(Long id) {
        Medecin medecin = getMedecinById(id);
        medecin.setActif(false);
        medecinDAO.save(medecin);
        auditService.log("DEACTIVATE", "Medecin", id, "Désactivation médecin: " + medecin.getNomComplet());
    }

    /**
     * Activer un médecin
     */
    public void activerMedecin(Long id) {
        Medecin medecin = getMedecinById(id);
        medecin.setActif(true);
        medecinDAO.save(medecin);
        auditService.log("ACTIVATE", "Medecin", id, "Activation médecin: " + medecin.getNomComplet());
    }

    /**
     * Supprimer définitivement un médecin (hard delete)
     */
    public void deleteMedecin(Long id) {
        Medecin medecin = getMedecinById(id);
        medecinDAO.hardDelete(id);
        auditService.log("DELETE", "Medecin", id, "Suppression définitive médecin: " + medecin.getNomComplet());
    }

    /**
     * Rechercher des médecins par mot-clé
     */
    public List<Medecin> searchMedecins(String keyword) {
        return medecinDAO.search(keyword);
    }

    /**
     * Mettre à jour la spécialité d'un médecin
     */
    public Medecin updateSpecialite(Long medecinId, Long specialiteId) {
        Medecin medecin = getMedecinById(medecinId);
        Specialite specialite = specialiteDAO.findById(specialiteId);

        if (specialite == null) {
            throw new RuntimeException("Spécialité non trouvée avec l'ID: " + specialiteId);
        }

        medecin.setSpecialite(specialite);
        Medecin updatedMedecin = medecinDAO.save(medecin);
        auditService.log("UPDATE_SPECIALITE", "Medecin", medecinId,
                "Mise à jour spécialité: " + specialite.getNom());
        return updatedMedecin;
    }

    /**
     * Mettre à jour la spécialité d'un médecin (version avec objet)
     */
    public Medecin updateSpecialite(Medecin medecin, Specialite specialite) {
        if (medecin == null || medecin.getId() == null) {
            throw new RuntimeException("Médecin invalide");
        }
        if (specialite == null || specialite.getId() == null) {
            throw new RuntimeException("Spécialité invalide");
        }

        medecin.setSpecialite(specialite);
        Medecin updatedMedecin = medecinDAO.save(medecin);
        auditService.log("UPDATE_SPECIALITE", "Medecin", medecin.getId(),
                "Mise à jour spécialité: " + specialite.getNom());
        return updatedMedecin;
    }

    /**
     * Obtenir les jours de disponibilité d'un médecin
     */
    public List<String> getJoursDisponibles(Long medecinId) {
        Medecin medecin = getMedecinById(medecinId);
        return medecin.getJoursDisponibles();  // Cette méthode doit exister dans Medecin.java
    }

    /**
     * Mettre à jour les jours de disponibilité d'un médecin
     */
    public Medecin updateJoursDisponibles(Long medecinId, List<String> joursDisponibles) {
        Medecin medecin = getMedecinById(medecinId);
        medecin.setJoursDisponibles(joursDisponibles);
        Medecin updatedMedecin = medecinDAO.save(medecin);
        auditService.log("UPDATE_DISPONIBILITES", "Medecin", medecinId,
                "Mise à jour jours disponibles");
        return updatedMedecin;
    }
}
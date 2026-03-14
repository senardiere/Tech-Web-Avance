package com.clinique.service;

import com.clinique.dao.SpecialiteDAO;
import com.clinique.entity.Specialite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecialiteService {

    @Autowired
    private SpecialiteDAO specialiteDAO;

    @Autowired
    private AuditService auditService;

    /**
     * Créer une nouvelle spécialité
     */
    public Specialite createSpecialite(Specialite specialite) {
        // Vérifications métier
        if (specialiteDAO.existsByNom(specialite.getNom())) {
            throw new RuntimeException("Une spécialité avec ce nom existe déjà");
        }
        if (specialite.getCode() != null && specialiteDAO.existsByCode(specialite.getCode())) {
            throw new RuntimeException("Une spécialité avec ce code existe déjà");
        }

        Specialite savedSpecialite = specialiteDAO.save(specialite);
        auditService.log("CREATE", "Specialite", savedSpecialite.getId(),
                "Création spécialité: " + savedSpecialite.getNom());  // Changé getLibelle() → getNom()
        return savedSpecialite;
    }

    /**
     * Mettre à jour une spécialité
     */
    public Specialite updateSpecialite(Specialite specialite) {
        Specialite existingSpecialite = specialiteDAO.findById(specialite.getId());
        if (existingSpecialite == null) {
            throw new RuntimeException("Spécialité non trouvée avec l'ID: " + specialite.getId());
        }

        // Vérifier l'unicité du nom si changé
        if (!existingSpecialite.getNom().equals(specialite.getNom()) &&
                specialiteDAO.existsByNom(specialite.getNom())) {
            throw new RuntimeException("Une spécialité avec ce nom existe déjà");
        }

        // Vérifier l'unicité du code si changé
        if (specialite.getCode() != null &&
                !existingSpecialite.getCode().equals(specialite.getCode()) &&
                specialiteDAO.existsByCode(specialite.getCode())) {
            throw new RuntimeException("Une spécialité avec ce code existe déjà");
        }

        Specialite updatedSpecialite = specialiteDAO.save(specialite);
        auditService.log("UPDATE", "Specialite", updatedSpecialite.getId(),
                "Mise à jour spécialité: " + updatedSpecialite.getNom());  // Changé getLibelle() → getNom()
        return updatedSpecialite;
    }

    /**
     * Trouver une spécialité par son ID
     */
    public Specialite getSpecialiteById(Long id) {
        Specialite specialite = specialiteDAO.findById(id);
        if (specialite == null) {
            throw new RuntimeException("Spécialité non trouvée avec l'ID: " + id);
        }
        return specialite;
    }

    /**
     * Trouver une spécialité par son nom
     */
    public Specialite getSpecialiteByNom(String nom) {
        return specialiteDAO.findByNom(nom);
    }

    /**
     * Trouver une spécialité par son code
     */
    public Specialite getSpecialiteByCode(String code) {
        return specialiteDAO.findByCode(code);
    }

    /**
     * Trouver toutes les spécialités
     */
    public List<Specialite> getAllSpecialites() {
        return specialiteDAO.findAll();
    }

    /**
     * Trouver toutes les spécialités actives
     */
    public List<Specialite> getSpecialitesActives() {
        return specialiteDAO.findAllActives();
    }

    /**
     * Compter le nombre total de spécialités
     */
    public long countSpecialites() {
        return specialiteDAO.count();
    }

    /**
     * Compter le nombre de spécialités actives
     */
    public long countSpecialitesActives() {
        return specialiteDAO.countActives();
    }

    /**
     * Désactiver une spécialité (soft delete)
     */
    public void desactiverSpecialite(Long id) {
        Specialite specialite = getSpecialiteById(id);
        specialite.setActif(false);
        specialiteDAO.save(specialite);
        auditService.log("DEACTIVATE", "Specialite", id,
                "Désactivation spécialité: " + specialite.getNom());  // Changé getLibelle() → getNom()
    }

    /**
     * Activer une spécialité
     */
    public void activerSpecialite(Long id) {
        Specialite specialite = getSpecialiteById(id);
        specialite.setActif(true);
        specialiteDAO.save(specialite);
        auditService.log("ACTIVATE", "Specialite", id,
                "Activation spécialité: " + specialite.getNom());  // Changé getLibelle() → getNom()
    }

    /**
     * Supprimer définitivement une spécialité (hard delete)
     */
    public void deleteSpecialite(Long id) {
        Specialite specialite = getSpecialiteById(id);

        // Vérifier si la spécialité est utilisée par des médecins
        if (specialiteDAO.countMedecinsBySpecialite(id) > 0) {
            throw new RuntimeException("Impossible de supprimer cette spécialité car elle est associée à des médecins");
        }

        specialiteDAO.hardDelete(id);
        auditService.log("DELETE", "Specialite", id,
                "Suppression définitive spécialité: " + specialite.getNom());  // Changé getLibelle() → getNom()
    }

    /**
     * Rechercher des spécialités par mot-clé
     */
    public List<Specialite> searchSpecialites(String keyword) {
        return specialiteDAO.search(keyword);
    }

    /**
     * Obtenir les statistiques des spécialités
     */
    public Object getSpecialiteStats(Long id) {
        Specialite specialite = getSpecialiteById(id);
        return specialiteDAO.getSpecialiteStats(id);
    }

    /**
     * Obtenir les médecins par spécialité
     */
    public List<Object[]> getMedecinsBySpecialite(Long id) {
        Specialite specialite = getSpecialiteById(id);
        return specialiteDAO.findMedecinsBySpecialite(id);
    }
}
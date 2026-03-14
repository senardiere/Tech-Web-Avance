package com.clinique.service;

import com.clinique.dao.PatientDAO;
import com.clinique.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientDAO patientDAO;

    @Autowired
    private AuditService auditService;

    /**
     * Créer un nouveau patient
     */
    public Patient createPatient(Patient patient) {
        Patient savedPatient = patientDAO.save(patient);
        auditService.log("CREATE", "Patient", savedPatient.getId(),
                "Création patient: " + savedPatient.getNomComplet());
        return savedPatient;
    }

    /**
     * Mettre à jour un patient
     */
    public Patient updatePatient(Patient patient) {
        Patient updatedPatient = patientDAO.save(patient);
        auditService.log("UPDATE", "Patient", updatedPatient.getId(),
                "Mise à jour patient: " + updatedPatient.getNomComplet());
        return updatedPatient;
    }

    /**
     * Trouver un patient par son ID
     */
    public Patient getPatientById(Long id) {
        return patientDAO.findById(id);
    }

    /**
     * Trouver tous les patients
     */
    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }

    /**
     * Trouver tous les patients actifs
     */
    public List<Patient> getAllPatientsActifs() {
        return patientDAO.findAllActifs();
    }

    /**
     * Compter le nombre total de patients
     */
    public long countPatients() {
        return patientDAO.count();
    }

    /**
     * Compter les patients par médecin
     */
    public long countPatientsByMedecin(Long medecinId) {
        return patientDAO.countByMedecinId(medecinId);
    }

    /**
     * Désactiver un patient
     */
    public void deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patientDAO.delete(id);
        auditService.log("DELETE", "Patient", id, "Désactivation patient: " + patient.getNomComplet());
    }

    /**
     * Rechercher des patients par nom
     */
    public List<Patient> rechercherParNom(String nom) {
        return patientDAO.rechercherParNom(nom);
    }

    /**
     * Obtenir les derniers patients
     */
    public List<Patient> getDerniersPatients(int limit) {
        return patientDAO.findLastPatients(limit);
    }

    /**
     * Obtenir les patients par médecin
     */
    public List<Patient> getPatientsByMedecin(Long medecinId) {
        return patientDAO.findByMedecinId(medecinId);
    }

    /**
     * Obtenir les derniers patients d'un médecin
     */
    public List<Patient> getDerniersPatientsByMedecin(Long medecinId, int limit) {
        return patientDAO.findLastByMedecinId(medecinId, limit);
    }

    /**
     * Rechercher des patients par mot-clé pour un médecin
     */
    public List<Patient> searchPatientsByMedecin(String keyword, Long medecinId) {
        return patientDAO.searchByMedecin(keyword, medecinId);
    }
// Ajoutez cette méthode dans votre PatientService.java existant

    /**
     * Compter les patients d'un médecin entre deux dates
     */
    @Transactional(readOnly = true)
    public long countPatientsByMedecinAndDateBetween(Long medecinId, LocalDate debut, LocalDate fin) {
        return patientDAO.countByMedecinAndDateBetween(medecinId, debut, fin);
    }
    /**
     * Mettre à jour la dernière visite d'un patient
     */
    public void updateDerniereVisite(Long patientId, LocalDate dateVisite) {
        Patient patient = getPatientById(patientId);
        if (patient != null) {
            patient.setDerniereVisite(dateVisite);
            patientDAO.save(patient);
            auditService.log("UPDATE_VISITE", "Patient", patientId,
                    "Dernière visite mise à jour: " + dateVisite);
        }
    }
}
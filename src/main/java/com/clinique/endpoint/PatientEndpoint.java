package com.clinique.endpoint;

import com.clinique.dao.PatientDAO;
import com.clinique.entity.Patient;
import com.clinique.entity.Medecin;
import com.clinique.dao.MedecinDAO;
import com.clinique.enums.StatutPatient;
import com.clinique.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/patients")
public class PatientEndpoint {

    @Autowired
    private PatientDAO patientDAO;

    @Autowired
    private MedecinDAO medecinDAO;

    @Autowired
    private AuditService auditService;

    @PostMapping
    public Map<String, Object> createPatient(@RequestBody Map<String, Object> data) {
        // Debug
        System.out.println("=== RÉCEPTION PATIENT JAVA ===");
        System.out.println("Données reçues: " + data);

        // Extraire les valeurs - Utiliser les noms envoyés par .NET
        String last_name = (String) data.get("last_name");     // ← reçu de .NET
        String first_name = (String) data.get("first_name");   // ← reçu de .NET

        System.out.println("last_name reçu = " + last_name);
        System.out.println("first_name reçu = " + first_name);

        // Validation
        if (last_name == null || last_name.trim().isEmpty()) {
            throw new RuntimeException("Le nom (last_name) est obligatoire");
        }
        if (first_name == null || first_name.trim().isEmpty()) {
            throw new RuntimeException("Le prénom (first_name) est obligatoire");
        }

        // Créer le patient avec les bons noms de propriétés
        Patient patient = new Patient();
        patient.setNom(last_name);      // last_name -> nom
        patient.setPrenom(first_name);  // first_name -> prenom

        patient.setEmail((String) data.get("email"));
        patient.setTelephone((String) data.get("telephone"));
        patient.setAdresse((String) data.get("adresse"));

        // Date de naissance - format "yyyy-MM-dd"
        String dateNaissanceStr = (String) data.get("date_naissance");
        if (dateNaissanceStr != null && !dateNaissanceStr.isEmpty()) {
            try {
                LocalDate dateNaissance = LocalDate.parse(dateNaissanceStr);
                patient.setDateNaissance(dateNaissance);
            } catch (Exception e) {
                System.out.println("Erreur parsing date: " + e.getMessage());
            }
        }

        patient.setNumeroSecuriteSociale((String) data.get("numero_securite_sociale"));
        patient.setMutuelle((String) data.get("mutuelle"));
        patient.setPersonneContact((String) data.get("personne_contact"));
        patient.setTelephoneContact((String) data.get("telephone_contact"));

        // Médecin traitant
        Object medecinIdObj = data.get("medecin_traitant_id");
        if (medecinIdObj != null) {
            Long medecinId = null;
            if (medecinIdObj instanceof Integer) {
                medecinId = ((Integer) medecinIdObj).longValue();
            } else if (medecinIdObj instanceof Long) {
                medecinId = (Long) medecinIdObj;
            }

            if (medecinId != null) {
                Medecin medecin = medecinDAO.findById(medecinId);
                if (medecin != null) {
                    patient.setMedecinTraitant(medecin);
                }
            }
        }

        // Statut
        String statutStr = (String) data.getOrDefault("statut", "ACTIF");
        try {
            patient.setStatut(StatutPatient.valueOf(statutStr));
        } catch (IllegalArgumentException e) {
            patient.setStatut(StatutPatient.ACTIF);
        }

        patient.setDateCreation(LocalDateTime.now());

        System.out.println("Patient à sauvegarder: nom=" + patient.getNom() +
                ", prenom=" + patient.getPrenom());

        Patient saved = patientDAO.save(patient);

        auditService.log("CREATE", "Patient", saved.getId(),
                "Création patient: " + saved.getNom() + " " + saved.getPrenom());

        // Retourner la réponse avec les noms attendus par .NET
        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("last_name", saved.getNom());
        response.put("first_name", saved.getPrenom());
        response.put("email", saved.getEmail());
        response.put("telephone", saved.getTelephone());
        response.put("statut", saved.getStatut().toString());

        return response;
    }

    // ========== GET ==========

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientDAO.findById(id);
    }

    @GetMapping("/actifs")
    public List<Patient> getPatientsActifs() {
        return patientDAO.findAllActifs();
    }

    @GetMapping("/search")
    public List<Patient> searchPatients(@RequestParam String nom) {
        return patientDAO.rechercherParNom(nom);
    }

    @GetMapping("/derniers")
    public List<Patient> getDerniersPatients(@RequestParam(defaultValue = "10") int limit) {
        return patientDAO.findLastPatients(limit);
    }

    @GetMapping("/medecin/{medecinId}")
    public List<Patient> getPatientsByMedecin(@PathVariable Long medecinId) {
        return patientDAO.findByMedecinId(medecinId);
    }

    @GetMapping("/medecin/{medecinId}/derniers")
    public List<Patient> getDerniersPatientsByMedecin(@PathVariable Long medecinId,
                                                      @RequestParam(defaultValue = "10") int limit) {
        return patientDAO.findLastByMedecinId(medecinId, limit);
    }

    @GetMapping("/medecin/{medecinId}/search")
    public List<Patient> searchPatientsByMedecin(@PathVariable Long medecinId,
                                                 @RequestParam String keyword) {
        return patientDAO.searchByMedecin(keyword, medecinId);
    }

    // ========== PUT ==========

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        Patient patient = patientDAO.findById(id);
        if (patient == null) {
            throw new RuntimeException("Patient non trouvé");
        }

        if (data.containsKey("last_name")) {
            patient.setNom((String) data.get("last_name"));
        }
        if (data.containsKey("first_name")) {
            patient.setPrenom((String) data.get("first_name"));
        }
        if (data.containsKey("email")) {
            patient.setEmail((String) data.get("email"));
        }
        if (data.containsKey("telephone")) {
            patient.setTelephone((String) data.get("telephone"));
        }
        if (data.containsKey("adresse")) {
            patient.setAdresse((String) data.get("adresse"));
        }
        if (data.containsKey("mutuelle")) {
            patient.setMutuelle((String) data.get("mutuelle"));
        }
        if (data.containsKey("personne_contact")) {
            patient.setPersonneContact((String) data.get("personne_contact"));
        }
        if (data.containsKey("telephone_contact")) {
            patient.setTelephoneContact((String) data.get("telephone_contact"));
        }

        return patientDAO.save(patient);
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientDAO.delete(id);
        auditService.log("DELETE", "Patient", id, "Suppression patient");
    }

    // ========== STATISTIQUES ==========

    @GetMapping("/stats/count")
    public long countPatients() {
        return patientDAO.count();
    }

    @GetMapping("/stats/count-by-medecin/{medecinId}")
    public long countPatientsByMedecin(@PathVariable Long medecinId) {
        return patientDAO.countByMedecinId(medecinId);
    }

    @GetMapping("/stats/count-by-medecin-date")
    public long countPatientsByMedecinAndDate(@RequestParam Long medecinId,
                                              @RequestParam String debut,
                                              @RequestParam String fin) {
        LocalDate debutDate = LocalDate.parse(debut);
        LocalDate finDate = LocalDate.parse(fin);
        return patientDAO.countByMedecinAndDateBetween(medecinId, debutDate, finDate);
    }
}
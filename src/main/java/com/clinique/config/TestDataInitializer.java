package com.clinique.config;

import com.clinique.dao.ConsultationDAO;
import com.clinique.dao.PatientDAO;
import com.clinique.dao.SpecialiteDAO;
import com.clinique.dao.UtilisateurDAO;
import com.clinique.entity.Admin;
import com.clinique.entity.Consultation;
import com.clinique.entity.Medecin;
import com.clinique.entity.Patient;
import com.clinique.entity.Specialite;
import com.clinique.enums.StatutConsultation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Initialise des données de test :
 * - un administrateur
 * - un médecin
 * - plusieurs spécialités
 * - un patient
 * - au moins une consultation passée
 */
@Component
public class TestDataInitializer implements CommandLineRunner {

    private final UtilisateurDAO utilisateurDAO;
    private final PatientDAO patientDAO;
    private final ConsultationDAO consultationDAO;
    private final SpecialiteDAO specialiteDAO;

    public TestDataInitializer(UtilisateurDAO utilisateurDAO,
                               PatientDAO patientDAO,
                               ConsultationDAO consultationDAO,
                               SpecialiteDAO specialiteDAO) {
        this.utilisateurDAO = utilisateurDAO;
        this.patientDAO = patientDAO;
        this.consultationDAO = consultationDAO;
        this.specialiteDAO = specialiteDAO;
    }

    @Override
    public void run(String... args) {
        // Admin de test
        if (utilisateurDAO.countAdmins() == 0) {
            Admin admin = new Admin(
                    "Admin",
                    "Principal",
                    "admin@clinique.local",
                    "admin",
                    "admin123"
            );
            admin.setDepartement("Direction");
            utilisateurDAO.save(admin);
            System.out.println("✔ Utilisateur admin de test créé (login='admin', motDePasse='admin123').");
        }

        // Spécialités de test (ajoutées si absentes, sans toucher aux existantes)
        if (!specialiteDAO.existsByCode("CARDIO")) {
            Specialite cardio = new Specialite("Cardiologie", "Spécialité du cœur et des vaisseaux sanguins");
            cardio.setCode("CARDIO");
            specialiteDAO.save(cardio);
            System.out.println("✔ Spécialité de test ajoutée : CARDIO - Cardiologie");
        }

        if (!specialiteDAO.existsByCode("DERMA")) {
            Specialite dermato = new Specialite("Dermatologie", "Spécialité de la peau, des cheveux et des ongles");
            dermato.setCode("DERMA");
            specialiteDAO.save(dermato);
            System.out.println("✔ Spécialité de test ajoutée : DERMA - Dermatologie");
        }

        if (!specialiteDAO.existsByCode("PED")) {
            Specialite pediatrie = new Specialite("Pédiatrie", "Spécialité médicale dédiée à l'enfant");
            pediatrie.setCode("PED");
            specialiteDAO.save(pediatrie);
            System.out.println("✔ Spécialité de test ajoutée : PED - Pédiatrie");
        }

        if (!specialiteDAO.existsByCode("GEN")) {
            Specialite generaliste = new Specialite("Médecine générale", "Médecin traitant polyvalent");
            generaliste.setCode("GEN");
            specialiteDAO.save(generaliste);
            System.out.println("✔ Spécialité de test ajoutée : GEN - Médecine générale");
        }

        // Médecin de test
        Medecin medecin;
        if (utilisateurDAO.countMedecins() == 0) {
            medecin = new Medecin(
                    "Dupont",
                    "Jean",
                    "jean.dupont@clinique.local",
                    "medecin",
                    "medecin123"
            );
            medecin.setNumeroLicence("LIC-0001");
            medecin.setCabinet("Cabinet A");
            // Associer une spécialité s'il en existe au moins une
            if (!specialiteDAO.findAllActives().isEmpty()) {
                medecin.setSpecialite(specialiteDAO.findAllActives().get(0));
            }
            medecin = (Medecin) utilisateurDAO.save(medecin);
            System.out.println("✔ Utilisateur médecin de test créé (login='medecin', motDePasse='medecin123').");
        } else {
            // Récupérer un médecin existant (le premier)
            medecin = (Medecin) utilisateurDAO.findMedecins().stream().findFirst().orElse(null);
        }

        if (medecin == null) {
            // Si aucun médecin n'est disponible, on s'arrête pour éviter des NullPointerException
            System.out.println("⚠ Aucun médecin disponible pour créer des consultations de test.");
            return;
        }

        // Patient de test
        Patient patient;
        if (patientDAO.count() == 0) {
            patient = new Patient();
            patient.setNom("Martin");
            patient.setPrenom("Claire");
            patient.setEmail("claire.martin@clinique.local");
            patient.setTelephone("0600000000");
            patient.setAdresse("10 Rue de la Santé");
            patient.setDateNaissance(LocalDate.of(1990, 5, 12));
            patient.setNumeroSecuriteSociale("1234567890123");
            patient.setMedecinTraitant(medecin);
            patient = patientDAO.save(patient);
            System.out.println("✔ Patient de test créé pour le Dr. " +
                    medecin.getPrenom() + " " + medecin.getNom());
        } else {
            patient = patientDAO.findLastPatients(1).stream().findFirst().orElse(null);
        }

        if (patient == null) {
            System.out.println("⚠ Aucun patient disponible pour créer des consultations de test.");
            return;
        }

        // Consultation de test (passée)
        if (consultationDAO.count() == 0) {
            Consultation consultation = new Consultation();
            consultation.setPatient(patient);
            consultation.setMedecin(medecin);
            consultation.setDiagnostic("Suivi de consultation de routine, tout est normal.");
            consultation.setPrescriptions("Vitamine D quotidienne pendant 3 mois.");
            consultation.setObservations("Patient en bonne santé générale.");

            // Consultation datée 7 jours dans le passé
            LocalDateTime dateConsultation = LocalDateTime.now().minusDays(7);
            consultation.setDateConsultation(dateConsultation);
            consultation.setStatut(StatutConsultation.TERMINEE);

            consultationDAO.save(consultation);
            System.out.println("✔ Consultation de test (passée) créée pour " +
                    patient.getPrenom() + " " + patient.getNom() +
                    " avec le Dr. " + medecin.getPrenom() + " " + medecin.getNom());
        }
    }
}


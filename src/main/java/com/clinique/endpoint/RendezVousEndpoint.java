package com.clinique.endpoint;

import com.clinique.entity.RendezVous;
import com.clinique.entity.Patient;
import com.clinique.entity.Medecin;
import com.clinique.enums.StatutRendezVous;
import com.clinique.service.RendezVousService;
import com.clinique.service.PatientService;
import com.clinique.service.MedecinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/rendezvous")
public class RendezVousEndpoint {

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedecinService medecinService;

    // Conversion manuelle pour éviter la boucle infinie
    private Map<String, Object> convertToMap(RendezVous r) {
        Map<String, Object> map = new HashMap<>();
        if (r == null) return map;

        map.put("id", r.getId());
        map.put("dateHeure", r.getDateHeure());
        map.put("duree", r.getDuree());
        map.put("statut", r.getStatut() != null ? r.getStatut().name() : null);
        map.put("motif", r.getMotif());
        map.put("motifAnnulation", r.getMotifAnnulation());
        map.put("datePrise", r.getDatePrise());
        map.put("dateValidation", r.getDateValidation());
        map.put("dateAnnulation", r.getDateAnnulation());

        if (r.getPatient() != null) {
            Map<String, Object> patient = new HashMap<>();
            patient.put("id", r.getPatient().getId());
            patient.put("nom", r.getPatient().getNom());
            patient.put("prenom", r.getPatient().getPrenom());
            map.put("patient", patient);
        }

        if (r.getMedecin() != null) {
            Map<String, Object> medecin = new HashMap<>();
            medecin.put("id", r.getMedecin().getId());
            medecin.put("nom", r.getMedecin().getNom());
            medecin.put("prenom", r.getMedecin().getPrenom());
            map.put("medecin", medecin);
        }

        if (r.getConsultation() != null) {
            Map<String, Object> consultation = new HashMap<>();
            consultation.put("id", r.getConsultation().getId());
            map.put("consultation", consultation);
        }

        return map;
    }

    // ========== GET ==========

    @GetMapping
    public List<Map<String, Object>> getAllRendezVous() {
        return rendezVousService.getAllRendezVous().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getRendezVousById(@PathVariable Long id) {
        RendezVous rendezVous = rendezVousService.getRendezVousById(id);
        return convertToMap(rendezVous);
    }

    @GetMapping("/patient/{patientId}")
    public List<Map<String, Object>> getRendezVousByPatient(@PathVariable Long patientId) {
        return rendezVousService.getRendezVousByPatient(patientId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}")
    public List<Map<String, Object>> getRendezVousByMedecin(@PathVariable Long medecinId) {
        return rendezVousService.getRendezVousByMedecin(medecinId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}/date")
    public List<Map<String, Object>> getRendezVousByMedecinAndDate(
            @PathVariable Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return rendezVousService.getRendezVousByMedecinAndDate(medecinId, date).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}/statut/{statut}")
    public List<Map<String, Object>> getRendezVousByMedecinAndStatut(
            @PathVariable Long medecinId,
            @PathVariable String statut) {
        StatutRendezVous statutEnum = StatutRendezVous.valueOf(statut);
        return rendezVousService.getRendezVousByMedecinAndStatut(medecinId, statutEnum).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/statut/{statut}")
    public List<Map<String, Object>> getRendezVousByStatut(@PathVariable String statut) {
        StatutRendezVous statutEnum = StatutRendezVous.valueOf(statut);
        return rendezVousService.getRendezVousByStatut(statutEnum).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/date")
    public List<Map<String, Object>> getRendezVousByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return rendezVousService.getRendezVousByDate(date).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/du-jour")
    public List<Map<String, Object>> getRendezVousDuJour() {
        return rendezVousService.getRendezVousDuJour().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/medecin/{medecinId}/date-between")
    public List<Map<String, Object>> getRendezVousByMedecinAndDateBetween(
            @PathVariable Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return rendezVousService.getRendezVousByMedecinAndDateBetween(medecinId, startDate, endDate).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/patient/{patientId}/medecin/{medecinId}")
    public List<Map<String, Object>> getRendezVousByPatientAndMedecin(
            @PathVariable Long patientId,
            @PathVariable Long medecinId) {
        return rendezVousService.getRendezVousByPatientAndMedecin(patientId, medecinId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/prochains/patient/{patientId}")
    public List<Map<String, Object>> getProchainsRendezVousPatient(@PathVariable Long patientId) {
        return rendezVousService.getProchainsRendezVousPatient(patientId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/prochains/medecin/{medecinId}")
    public List<Map<String, Object>> getProchainsRendezVousMedecin(
            @PathVariable Long medecinId,
            @RequestParam(defaultValue = "10") int limit) {
        return rendezVousService.getProchainsRendezVousMedecin(medecinId, limit).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    // ========== POST ==========

    @PostMapping
    public Map<String, Object> createRendezVous(@RequestBody Map<String, Object> request) {
        System.out.println("=== CRÉATION RENDEZ-VOUS JAVA ===");
        System.out.println("Données reçues: " + request);

        // Extraire les valeurs
        Object patientIdObj = request.get("patient_id");
        Object medecinIdObj = request.get("medecin_id");
        String dateHeureStr = (String) request.get("date_heure");
        Integer duree = null;
        if (request.get("duree") instanceof Integer) {
            duree = (Integer) request.get("duree");
        } else if (request.get("duree") instanceof String) {
            duree = Integer.parseInt((String) request.get("duree"));
        }
        String motif = (String) request.get("motif");
        String statutStr = (String) request.get("statut");
        String datePriseStr = (String) request.get("date_prise");

        // Validation
        if (patientIdObj == null) {
            throw new IllegalArgumentException("Le patient est obligatoire");
        }
        if (medecinIdObj == null) {
            throw new IllegalArgumentException("Le médecin est obligatoire");
        }
        if (dateHeureStr == null || dateHeureStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La date du rendez-vous est obligatoire");
        }

        Long patientId = null;
        Long medecinId = null;

        if (patientIdObj instanceof Integer) {
            patientId = ((Integer) patientIdObj).longValue();
        } else if (patientIdObj instanceof Long) {
            patientId = (Long) patientIdObj;
        }

        if (medecinIdObj instanceof Integer) {
            medecinId = ((Integer) medecinIdObj).longValue();
        } else if (medecinIdObj instanceof Long) {
            medecinId = (Long) medecinIdObj;
        }

        // Récupérer le patient et le médecin
        Patient patient = patientService.getPatientById(patientId);
        Medecin medecin = medecinService.getMedecinById(medecinId);

        if (patient == null) {
            throw new IllegalArgumentException("Patient non trouvé avec ID: " + patientId);
        }
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin non trouvé avec ID: " + medecinId);
        }

        // Parser la date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateHeure = LocalDateTime.parse(dateHeureStr, formatter);
        LocalDateTime datePrise = datePriseStr != null ? LocalDateTime.parse(datePriseStr, formatter) : LocalDateTime.now();

        // Créer le rendez-vous
        RendezVous rendezVous = new RendezVous();
        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        rendezVous.setDateHeure(dateHeure);
        rendezVous.setDuree(duree != null ? duree : 30);
        rendezVous.setMotif(motif);
        rendezVous.setDatePrise(datePrise);

        // Définir le statut
        if (statutStr != null) {
            try {
                rendezVous.setStatut(StatutRendezVous.valueOf(statutStr));
            } catch (IllegalArgumentException e) {
                rendezVous.setStatut(StatutRendezVous.PREVU);
            }
        } else {
            rendezVous.setStatut(StatutRendezVous.PREVU);
        }

        RendezVous saved = rendezVousService.createRendezVous(rendezVous);
        System.out.println("Rendez-vous créé avec ID: " + saved.getId());

        return convertToMap(saved);
    }

    // ========== PATCH ==========

    @PatchMapping("/{id}/valider")
    public void validerRendezVous(@PathVariable Long id) {
        rendezVousService.validerRendezVous(id);
    }

    @PatchMapping("/{id}/annuler")
    public void annulerRendezVous(@PathVariable Long id, @RequestParam String motif) {
        rendezVousService.annulerRendezVous(id, motif);
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    public void deleteRendezVous(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
    }

    // ========== VÉRIFICATIONS ==========

    @GetMapping("/check-disponibilite")
    public boolean isCreneauDisponible(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeure,
            @RequestParam(defaultValue = "30") int duree) {
        return rendezVousService.isCreneauDisponible(medecinId, dateHeure, duree);
    }

    // ========== STATISTIQUES ==========

    @GetMapping("/stats/count")
    public long countRendezVous() {
        return rendezVousService.countRendezVous();
    }

    @GetMapping("/stats/count-by-statut")
    public long countByStatut(@RequestParam String statut) {
        StatutRendezVous statutEnum = StatutRendezVous.valueOf(statut);
        return rendezVousService.countByStatut(statutEnum);
    }

    @GetMapping("/stats/count-du-jour")
    public long countRendezVousDuJour() {
        return rendezVousService.countRendezVousDuJour();
    }

    @GetMapping("/stats/count-by-medecin-date")
    public long countRendezVousByMedecinAndDate(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return rendezVousService.countRendezVousByMedecinAndDate(medecinId, date);
    }

    @GetMapping("/stats/count-by-medecin-date-between")
    public long countRendezVousByMedecinAndDateBetween(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return rendezVousService.countRendezVousByMedecinAndDateBetween(medecinId, debut, fin);
    }

    @GetMapping("/stats/count-by-medecin-week")
    public long countRendezVousByMedecinAndWeek(@RequestParam Long medecinId) {
        return rendezVousService.countRendezVousByMedecinAndWeek(medecinId);
    }

    @GetMapping("/stats/count-by-medecin-statut")
    public long countRendezVousByMedecinAndStatut(
            @RequestParam Long medecinId,
            @RequestParam String statut) {
        StatutRendezVous statutEnum = StatutRendezVous.valueOf(statut);
        return rendezVousService.countRendezVousByMedecinAndStatut(medecinId, statutEnum);
    }
}
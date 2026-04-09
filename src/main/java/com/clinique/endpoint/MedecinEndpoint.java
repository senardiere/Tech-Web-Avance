package com.clinique.endpoint;

import com.clinique.entity.Medecin;
import com.clinique.entity.Specialite;
import com.clinique.service.MedecinService;
import com.clinique.service.SpecialiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/medecins")
public class MedecinEndpoint {

    @Autowired
    private MedecinService medecinService;

    @Autowired
    private SpecialiteService specialiteService;

    // Conversion manuelle pour éviter la boucle infinie
    private Map<String, Object> convertToMap(Medecin m) {
        Map<String, Object> map = new HashMap<>();
        if (m == null) return map;

        map.put("id", m.getId());
        map.put("nom", m.getNom());
        map.put("prenom", m.getPrenom());
        map.put("email", m.getEmail());
        map.put("login", m.getLogin());
        map.put("numeroLicence", m.getNumeroLicence());
        map.put("cabinet", m.getCabinet());
        map.put("joursDisponibles", m.getJoursDisponibles());
        map.put("actif", m.isActif());
        map.put("dateCreation", m.getDateCreation());

        if (m.getSpecialite() != null) {
            Map<String, Object> specialite = new HashMap<>();
            specialite.put("id", m.getSpecialite().getId());
            specialite.put("nom", m.getSpecialite().getNom());
            map.put("specialite", specialite);
        }

        return map;
    }

    // ========== GET ==========

    @GetMapping
    public List<Map<String, Object>> getAllMedecins() {
        return medecinService.getAllMedecins().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/actifs")
    public List<Map<String, Object>> getMedecinsActifs() {
        return medecinService.getMedecinsActifs().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getMedecinById(@PathVariable Long id) {
        Medecin medecin = medecinService.getMedecinById(id);
        return convertToMap(medecin);
    }

    @GetMapping("/login/{login}")
    public Map<String, Object> getMedecinByLogin(@PathVariable String login) {
        Medecin medecin = medecinService.getMedecinByLogin(login);
        return convertToMap(medecin);
    }

    @GetMapping("/email/{email}")
    public Map<String, Object> getMedecinByEmail(@PathVariable String email) {
        Medecin medecin = medecinService.getMedecinByEmail(email);
        return convertToMap(medecin);
    }

    @GetMapping("/numero-licence/{numeroLicence}")
    public Map<String, Object> getMedecinByNumeroLicence(@PathVariable String numeroLicence) {
        Medecin medecin = medecinService.getMedecinByNumeroLicence(numeroLicence);
        return convertToMap(medecin);
    }

    @GetMapping("/specialite/{specialiteId}")
    public List<Map<String, Object>> getMedecinsBySpecialite(@PathVariable Long specialiteId) {
        return medecinService.getMedecinsBySpecialite(specialiteId).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchMedecins(@RequestParam String keyword) {
        return medecinService.searchMedecins(keyword).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/jours-disponibles")
    public List<String> getJoursDisponibles(@PathVariable Long id) {
        return medecinService.getJoursDisponibles(id);
    }

    // ========== POST ==========

    @PostMapping
    public Map<String, Object> createMedecin(@RequestBody Map<String, Object> request) {
        System.out.println("=== CRÉATION MÉDECIN ===");
        System.out.println("Données reçues: " + request);

        // Extraire les valeurs (snake_case de .NET)
        String last_name = (String) request.get("last_name");
        String first_name = (String) request.get("first_name");
        String email = (String) request.get("email");
        String login = (String) request.get("login");
        String mot_de_passe = (String) request.get("mot_de_passe");
        String numero_licence = (String) request.get("numero_licence");
        String cabinet = (String) request.get("cabinet");
        Object specialite_id_obj = request.get("specialite_id");

        // Validation
        if (last_name == null || last_name.trim().isEmpty()) {
            throw new RuntimeException("last_name est obligatoire");
        }
        if (first_name == null || first_name.trim().isEmpty()) {
            throw new RuntimeException("first_name est obligatoire");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("email est obligatoire");
        }
        if (login == null || login.trim().isEmpty()) {
            throw new RuntimeException("login est obligatoire");
        }
        if (mot_de_passe == null || mot_de_passe.trim().isEmpty()) {
            throw new RuntimeException("mot_de_passe est obligatoire");
        }

        // Créer le médecin
        Medecin medecin = new Medecin();
        medecin.setNom(last_name);
        medecin.setPrenom(first_name);
        medecin.setEmail(email);
        medecin.setLogin(login);
        medecin.setMotDePasse(mot_de_passe);
        medecin.setActif(true);
        medecin.setDateCreation(LocalDateTime.now());

        if (numero_licence != null && !numero_licence.trim().isEmpty()) {
            medecin.setNumeroLicence(numero_licence);
        }
        if (cabinet != null && !cabinet.trim().isEmpty()) {
            medecin.setCabinet(cabinet);
        }

        // Gérer la spécialité
        if (specialite_id_obj != null) {
            Long specialite_id = null;
            if (specialite_id_obj instanceof Integer) {
                specialite_id = ((Integer) specialite_id_obj).longValue();
            } else if (specialite_id_obj instanceof Long) {
                specialite_id = (Long) specialite_id_obj;
            }
            if (specialite_id != null) {
                try {
                    Specialite specialite = specialiteService.getSpecialiteById(specialite_id);
                    if (specialite != null) {
                        medecin.setSpecialite(specialite);
                        System.out.println("Spécialité assignée: " + specialite.getNom());
                    }
                } catch (Exception e) {
                    System.out.println("Spécialité non trouvée pour ID: " + specialite_id);
                }
            }
        }

        Medecin saved = medecinService.createMedecin(medecin);
        System.out.println("Médecin créé avec ID: " + saved.getId());

        return convertToMap(saved);
    }

    // ========== PUT ==========

    @PutMapping("/{id}")
    public Medecin updateMedecin(@PathVariable Long id, @RequestBody Medecin medecin) {
        medecin.setId(id);
        return medecinService.updateMedecin(medecin);
    }

    @PutMapping("/{id}/specialite/{specialiteId}")
    public Medecin updateSpecialite(@PathVariable Long id, @PathVariable Long specialiteId) {
        return medecinService.updateSpecialite(id, specialiteId);
    }

    @PutMapping("/{id}/jours-disponibles")
    public Medecin updateJoursDisponibles(@PathVariable Long id, @RequestBody List<String> joursDisponibles) {
        return medecinService.updateJoursDisponibles(id, joursDisponibles);
    }

    // ========== PATCH ==========

    @PatchMapping("/{id}/activer")
    public void activerMedecin(@PathVariable Long id) {
        medecinService.activerMedecin(id);
    }

    @PatchMapping("/{id}/desactiver")
    public void desactiverMedecin(@PathVariable Long id) {
        medecinService.desactiverMedecin(id);
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    public void deleteMedecin(@PathVariable Long id) {
        medecinService.deleteMedecin(id);
    }

    // ========== STATISTIQUES ==========

    @GetMapping("/stats/count")
    public long countMedecins() {
        return medecinService.countMedecins();
    }

    @GetMapping("/stats/count-actifs")
    public long countMedecinsActifs() {
        return medecinService.countMedecinsActifs();
    }

    @GetMapping("/stats/count-by-specialite/{specialiteId}")
    public long countMedecinsBySpecialite(@PathVariable Long specialiteId) {
        return medecinService.countMedecinsBySpecialite(specialiteId);
    }
}
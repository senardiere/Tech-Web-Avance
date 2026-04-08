package com.clinique.endpoint;

import com.clinique.dao.AuditLogDAO;
import com.clinique.entity.AuditLog;
import com.clinique.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/audit")
public class AuditEndpoint {

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditLogDAO auditLogDAO;  // ← AJOUTER CETTE LIGNE

    private Map<String, Object> convertToMap(AuditLog log) {
        Map<String, Object> map = new HashMap<>();
        if (log == null) return map;

        map.put("id", log.getId());
        map.put("utilisateur", log.getUtilisateur());
        map.put("action", log.getAction());
        map.put("entite", log.getEntite());
        map.put("entiteId", log.getEntiteId());
        map.put("details", log.getDetails());
        map.put("adresseIp", log.getAdresseIp());
        map.put("dateAction", log.getDateAction());

        return map;
    }

    @GetMapping
    public List<Map<String, Object>> getAllLogs(@RequestParam(defaultValue = "100") int limit) {
        List<AuditLog> logs = auditLogDAO.findAll(limit);
        return logs.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/utilisateur/{utilisateur}")
    public List<Map<String, Object>> getLogsByUtilisateur(
            @PathVariable String utilisateur,
            @RequestParam(defaultValue = "100") int limit) {
        List<AuditLog> logs = auditLogDAO.findByUtilisateur(utilisateur, limit);
        return logs.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/stats/count")
    public long countLogs() {
        return auditLogDAO.count();
    }

    @PostMapping("/log")
    public void createLog(@RequestBody Map<String, Object> logData) {
        String action = (String) logData.get("action");
        String entite = (String) logData.get("entite");
        Long entiteId = logData.get("entiteId") != null ? Long.valueOf(logData.get("entiteId").toString()) : null;
        String details = (String) logData.get("details");
        String utilisateur = (String) logData.get("utilisateur");

        if (utilisateur != null && !utilisateur.isEmpty()) {
            auditService.logFromApi(action, entite, entiteId, details, utilisateur);
        } else {
            auditService.log(action, entite, entiteId, details);
        }
    }
}
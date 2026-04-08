using Clinique.Api.DTOs;
using System.Text.Json;

namespace Clinique.Api.Mappers;

public static class ConsultationMapper
{
    public static ConsultationDto ToDto(JsonElement javaEntity)
    {
        if (javaEntity.ValueKind == JsonValueKind.Null)
            return null!;

        // Extraction du patient
        long patientId = 0;
        string patientNom = string.Empty;
        string patientPrenom = string.Empty;
        
        if (javaEntity.TryGetProperty("patient", out var patient))
        {
            if (patient.TryGetProperty("id", out var patientIdProp))
                patientId = patientIdProp.GetInt64();
            
            patientNom = patient.TryGetProperty("nom", out var nom) 
                ? nom.GetString() ?? string.Empty : string.Empty;
            
            patientPrenom = patient.TryGetProperty("prenom", out var prenom) 
                ? prenom.GetString() ?? string.Empty : string.Empty;
        }

        // Extraction du médecin
        long medecinId = 0;
        string medecinNom = string.Empty;
        string medecinPrenom = string.Empty;
        
        if (javaEntity.TryGetProperty("medecin", out var medecin))
        {
            if (medecin.TryGetProperty("id", out var medecinIdProp))
                medecinId = medecinIdProp.GetInt64();
            
            medecinNom = medecin.TryGetProperty("nom", out var nom) 
                ? nom.GetString() ?? string.Empty : string.Empty;
            
            medecinPrenom = medecin.TryGetProperty("prenom", out var prenom) 
                ? prenom.GetString() ?? string.Empty : string.Empty;
        }

        // Extraction du rendez-vous
        long? rendezVousId = null;
        if (javaEntity.TryGetProperty("rendezVous", out var rdv) && rdv.ValueKind != JsonValueKind.Null)
        {
            if (rdv.TryGetProperty("id", out var rdvIdProp))
                rendezVousId = rdvIdProp.GetInt64();
        }

        // Extraction de l'ID principal
        long consultationId = 0;
        if (javaEntity.TryGetProperty("id", out var consultationIdProp))
            consultationId = consultationIdProp.GetInt64();

        // Extraction de la date
        DateTime dateConsultation = DateTime.Now;
        if (javaEntity.TryGetProperty("dateConsultation", out var dateProp))
            dateConsultation = dateProp.GetDateTime();

        // Extraction du statut
        string statut = string.Empty;
        if (javaEntity.TryGetProperty("statut", out var statutProp))
            statut = statutProp.GetString() ?? string.Empty;

        // Extraction des autres propriétés
        string? motif = null;
        if (javaEntity.TryGetProperty("motif", out var motifProp))
            motif = motifProp.GetString();

        string? diagnostic = null;
        if (javaEntity.TryGetProperty("diagnostic", out var diagProp))
            diagnostic = diagProp.GetString();

        string? prescription = null;
        if (javaEntity.TryGetProperty("prescription", out var prescProp))
            prescription = prescProp.GetString();

        double? montant = null;
        if (javaEntity.TryGetProperty("montant", out var montantProp))
            montant = montantProp.GetDouble();

        return new ConsultationDto
        {
            Id = consultationId,
            PatientId = patientId,
            PatientNom = patientNom,
            PatientPrenom = patientPrenom,
            MedecinId = medecinId,
            MedecinNom = medecinNom,
            MedecinPrenom = medecinPrenom,
            RendezVousId = rendezVousId,
            DateConsultation = dateConsultation,
            Statut = statut,
            Motif = motif,
            Diagnostic = diagnostic,
            Prescription = prescription,
            Montant = montant
        };
    }

    public static List<ConsultationDto> ToDtoList(List<JsonElement> javaEntities)
    {
        if (javaEntities == null || javaEntities.Count == 0)
            return new List<ConsultationDto>();
            
        return javaEntities.Select(ToDto).ToList();
    }
}
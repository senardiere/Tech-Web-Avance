using Clinique.Api.DTOs;
using System.Text.Json;

namespace Clinique.Api.Mappers;

public static class ConsultationMapper
{
    public static ConsultationDto ToDto(JsonElement javaEntity)
    {
        if (javaEntity.ValueKind == JsonValueKind.Null)
            return null!;

        var dto = new ConsultationDto();

        // Extraction sécurisée de chaque champ avec try-catch
        try
        {
            // Extraction du patient
            if (javaEntity.TryGetProperty("patient", out var patient) && patient.ValueKind != JsonValueKind.Null)
            {
                if (patient.TryGetProperty("id", out var patientIdProp) && patientIdProp.ValueKind != JsonValueKind.Null)
                {
                    if (patientIdProp.ValueKind == JsonValueKind.Number)
                        dto.PatientId = patientIdProp.GetInt64();
                    else if (patientIdProp.ValueKind == JsonValueKind.String)
                        long.TryParse(patientIdProp.GetString(), out long patientId);
                }
                
                dto.PatientNom = patient.TryGetProperty("nom", out var nom) && nom.ValueKind != JsonValueKind.Null
                    ? nom.GetString() ?? string.Empty : string.Empty;
                
                dto.PatientPrenom = patient.TryGetProperty("prenom", out var prenom) && prenom.ValueKind != JsonValueKind.Null
                    ? prenom.GetString() ?? string.Empty : string.Empty;
            }
        }
        catch { }

        try
        {
            // Extraction du médecin
            if (javaEntity.TryGetProperty("medecin", out var medecin) && medecin.ValueKind != JsonValueKind.Null)
            {
                if (medecin.TryGetProperty("id", out var medecinIdProp) && medecinIdProp.ValueKind != JsonValueKind.Null)
                {
                    if (medecinIdProp.ValueKind == JsonValueKind.Number)
                        dto.MedecinId = medecinIdProp.GetInt64();
                    else if (medecinIdProp.ValueKind == JsonValueKind.String)
                        long.TryParse(medecinIdProp.GetString(), out long medecinId);
                }
                
                dto.MedecinNom = medecin.TryGetProperty("nom", out var nom) && nom.ValueKind != JsonValueKind.Null
                    ? nom.GetString() ?? string.Empty : string.Empty;
                
                dto.MedecinPrenom = medecin.TryGetProperty("prenom", out var prenom) && prenom.ValueKind != JsonValueKind.Null
                    ? prenom.GetString() ?? string.Empty : string.Empty;
            }
        }
        catch { }

        try
        {
            // Extraction de l'ID principal
            if (javaEntity.TryGetProperty("id", out var consultationIdProp) && consultationIdProp.ValueKind != JsonValueKind.Null)
            {
                if (consultationIdProp.ValueKind == JsonValueKind.Number)
                    dto.Id = consultationIdProp.GetInt64();
                else if (consultationIdProp.ValueKind == JsonValueKind.String)
                    long.TryParse(consultationIdProp.GetString(), out long id);
            }
        }
        catch { }

        try
        {
            // Extraction de la date
            if (javaEntity.TryGetProperty("dateConsultation", out var dateProp) && dateProp.ValueKind == JsonValueKind.String)
            {
                DateTime.TryParse(dateProp.GetString(), out DateTime dateConsultation);
                dto.DateConsultation = dateConsultation;
            }
        }
        catch { }

        try
        {
            // Extraction du statut
            if (javaEntity.TryGetProperty("statut", out var statutProp) && statutProp.ValueKind != JsonValueKind.Null)
                dto.Statut = statutProp.GetString() ?? string.Empty;
        }
        catch { }

        try
        {
            // Extraction des autres propriétés
            if (javaEntity.TryGetProperty("motif", out var motifProp) && motifProp.ValueKind != JsonValueKind.Null)
                dto.Motif = motifProp.GetString();

            if (javaEntity.TryGetProperty("diagnostic", out var diagProp) && diagProp.ValueKind != JsonValueKind.Null)
                dto.Diagnostic = diagProp.GetString();

            if (javaEntity.TryGetProperty("observations", out var obsProp) && obsProp.ValueKind != JsonValueKind.Null)
                dto.Observations = obsProp.GetString();

            if (javaEntity.TryGetProperty("prescriptions", out var prescProp) && prescProp.ValueKind != JsonValueKind.Null)
                dto.Prescription = prescProp.GetString();

            if (javaEntity.TryGetProperty("montant", out var montantProp) && montantProp.ValueKind != JsonValueKind.Null && montantProp.ValueKind == JsonValueKind.Number)
                dto.Montant = montantProp.GetDouble();

            if (javaEntity.TryGetProperty("poids", out var poidsProp) && poidsProp.ValueKind != JsonValueKind.Null && poidsProp.ValueKind == JsonValueKind.Number)
                dto.Poids = poidsProp.GetDouble();

            if (javaEntity.TryGetProperty("taille", out var tailleProp) && tailleProp.ValueKind != JsonValueKind.Null)
            {
                if (tailleProp.ValueKind == JsonValueKind.Number)
                    dto.Taille = tailleProp.GetInt32();
                else if (tailleProp.ValueKind == JsonValueKind.String)
                    int.TryParse(tailleProp.GetString(), out int taille);
            }

            if (javaEntity.TryGetProperty("tension", out var tensionProp) && tensionProp.ValueKind != JsonValueKind.Null)
                dto.Tension = tensionProp.GetString();
        }
        catch { }

        return dto;
    }

    public static List<ConsultationDto> ToDtoList(List<JsonElement> javaEntities)
    {
        var result = new List<ConsultationDto>();
        if (javaEntities != null)
        {
            foreach (var entity in javaEntities)
            {
                try
                {
                    result.Add(ToDto(entity));
                }
                catch { }
            }
        }
        return result;
    }
}
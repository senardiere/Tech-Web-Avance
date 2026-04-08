using Clinique.Api.DTOs;
using System.Text.Json;

namespace Clinique.Api.Mappers;

public static class RendezVousMapper
{
    private static long GetLong(JsonElement element, string propertyName)
    {
        return element.TryGetProperty(propertyName, out var value) && value.ValueKind != JsonValueKind.Null
            ? value.GetInt64() : 0;
    }
    
    private static string GetString(JsonElement element, string propertyName)
    {
        return element.TryGetProperty(propertyName, out var value) && value.ValueKind != JsonValueKind.Null
            ? value.GetString() ?? string.Empty : string.Empty;
    }
    
    private static string? GetNullableString(JsonElement element, string propertyName)
    {
        return element.TryGetProperty(propertyName, out var value) && value.ValueKind != JsonValueKind.Null
            ? value.GetString() : null;
    }
    
    private static int GetInt(JsonElement element, string propertyName)
    {
        return element.TryGetProperty(propertyName, out var value) && value.ValueKind != JsonValueKind.Null
            ? value.GetInt32() : 0;
    }
    
    private static DateTime? GetNullableDateTime(JsonElement element, string propertyName)
    {
        if (element.TryGetProperty(propertyName, out var value) && value.ValueKind != JsonValueKind.Null)
        {
            try
            {
                return value.GetDateTime();
            }
            catch
            {
                return null;
            }
        }
        return null;
    }
    
    private static DateTime GetDateTime(JsonElement element, string propertyName)
    {
        if (element.TryGetProperty(propertyName, out var value) && value.ValueKind != JsonValueKind.Null)
        {
            try
            {
                return value.GetDateTime();
            }
            catch
            {
                return DateTime.Now;
            }
        }
        return DateTime.Now;
    }
    
    public static RendezVousDto ToDto(JsonElement javaEntity)
    {
        if (javaEntity.ValueKind == JsonValueKind.Null)
            return null!;
        
        var dto = new RendezVousDto
        {
            Id = GetLong(javaEntity, "id"),
            DateHeure = GetDateTime(javaEntity, "dateHeure"),
            Duree = GetInt(javaEntity, "duree"),
            Statut = GetString(javaEntity, "statut"),
            Motif = GetNullableString(javaEntity, "motif"),
            MotifAnnulation = GetNullableString(javaEntity, "motifAnnulation"),
            DatePrise = GetNullableDateTime(javaEntity, "datePrise"),
            DateValidation = GetNullableDateTime(javaEntity, "dateValidation"),
            DateAnnulation = GetNullableDateTime(javaEntity, "dateAnnulation")
        };
        
        if (javaEntity.TryGetProperty("patient", out var patient) && patient.ValueKind != JsonValueKind.Null)
        {
            dto.Patient = new RendezVousPatientDto
            {
                Id = GetLong(patient, "id"),
                Nom = GetString(patient, "nom"),
                Prenom = GetString(patient, "prenom")
            };
        }
        
        if (javaEntity.TryGetProperty("medecin", out var medecin) && medecin.ValueKind != JsonValueKind.Null)
        {
            dto.Medecin = new RendezVousMedecinDto
            {
                Id = GetLong(medecin, "id"),
                Nom = GetString(medecin, "nom"),
                Prenom = GetString(medecin, "prenom")
            };
        }
        
        if (javaEntity.TryGetProperty("consultation", out var consultation) && consultation.ValueKind != JsonValueKind.Null)
        {
            dto.Consultation = new RendezVousConsultationDto
            {
                Id = GetLong(consultation, "id")
            };
        }
        
        return dto;
    }
    
    public static List<RendezVousDto> ToDtoList(List<JsonElement> javaEntities)
    {
        if (javaEntities == null || javaEntities.Count == 0)
            return new List<RendezVousDto>();
            
        return javaEntities.Select(ToDto).ToList();
    }
}
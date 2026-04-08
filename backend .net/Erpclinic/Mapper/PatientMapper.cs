using Clinique.Api.DTOs;
using System.Text.Json;

namespace Clinique.Api.Mappers;

public static class PatientMapper
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
    
    public static PatientDto ToDto(JsonElement javaEntity)
    {
        if (javaEntity.ValueKind == JsonValueKind.Null)
            return null!;
        
        var dto = new PatientDto
        {
            Id = GetLong(javaEntity, "id"),
            Nom = GetString(javaEntity, "nom"),
            Prenom = GetString(javaEntity, "prenom"),
            Email = GetString(javaEntity, "email"),
            Telephone = GetString(javaEntity, "telephone"),
            Adresse = GetString(javaEntity, "adresse"),
            NumeroSecuriteSociale = GetNullableString(javaEntity, "numeroSecuriteSociale"),
            Mutuelle = GetNullableString(javaEntity, "mutuelle"),
            PersonneContact = GetNullableString(javaEntity, "personneContact"),
            TelephoneContact = GetNullableString(javaEntity, "telephoneContact"),
            Statut = GetString(javaEntity, "statut"),
            DateNaissance = GetNullableDateTime(javaEntity, "dateNaissance"),
            DateCreation = GetDateTime(javaEntity, "dateCreation"),
            DerniereVisite = GetNullableDateTime(javaEntity, "derniereVisite")
        };
        
        if (javaEntity.TryGetProperty("medecinTraitant", out var medecin) && medecin.ValueKind != JsonValueKind.Null)
        {
            dto.MedecinTraitant = new MedecinRefDto
            {
                Id = GetLong(medecin, "id"),
                Nom = GetString(medecin, "nom"),
                Prenom = GetString(medecin, "prenom")
            };
        }
        
        return dto;
    }
    
    public static List<PatientDto> ToDtoList(List<JsonElement> javaEntities)
    {
        if (javaEntities == null || javaEntities.Count == 0)
            return new List<PatientDto>();
            
        var result = new List<PatientDto>();
        foreach (var entity in javaEntities)
        {
            try
            {
                result.Add(ToDto(entity));
            }
            catch (Exception)
            {
                
            }
        }
        return result;
    }
}
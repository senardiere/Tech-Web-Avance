using Clinique.Api.DTOs;
using System.Text.Json;

namespace Clinique.Api.Mappers;

public static class MedecinMapper
{
    public static MedecinDto ToDto(JsonElement javaEntity)
    {
        if (javaEntity.ValueKind == JsonValueKind.Null)
            return null!;
        
        var dto = new MedecinDto
        {
            Id = javaEntity.TryGetProperty("id", out var id) ? id.GetInt64() : 0,
            Nom = javaEntity.TryGetProperty("nom", out var nom) ? nom.GetString() ?? string.Empty : string.Empty,
            Prenom = javaEntity.TryGetProperty("prenom", out var prenom) ? prenom.GetString() ?? string.Empty : string.Empty,
            Email = javaEntity.TryGetProperty("email", out var email) ? email.GetString() ?? string.Empty : string.Empty,
            Login = javaEntity.TryGetProperty("login", out var login) ? login.GetString() ?? string.Empty : string.Empty,
            NumeroLicence = javaEntity.TryGetProperty("numeroLicence", out var licence) ? licence.GetString() ?? string.Empty : string.Empty,
            Cabinet = javaEntity.TryGetProperty("cabinet", out var cabinet) ? cabinet.GetString() ?? string.Empty : string.Empty,
            Actif = javaEntity.TryGetProperty("actif", out var actif) ? actif.GetBoolean() : false,
            JoursDisponibles = javaEntity.TryGetProperty("joursDisponibles", out var jours) 
                ? jours.EnumerateArray().Select(j => j.GetString() ?? string.Empty).ToList() 
                : new List<string>()
        };
        
        if (javaEntity.TryGetProperty("dateCreation", out var dateCreation))
            dto.DateCreation = dateCreation.GetDateTime();
        
        if (javaEntity.TryGetProperty("specialite", out var specialite) && specialite.ValueKind != JsonValueKind.Null)
        {
            dto.Specialite = new SpecialiteDto
            {
                Id = specialite.TryGetProperty("id", out var specId) ? specId.GetInt64() : 0,
                Nom = specialite.TryGetProperty("nom", out var specNom) ? specNom.GetString() ?? string.Empty : string.Empty
            };
        }
        
        return dto;
    }
    
    public static List<MedecinDto> ToDtoList(List<JsonElement> javaEntities)
    {
        return javaEntities?.Select(ToDto).ToList() ?? new List<MedecinDto>();
    }
}
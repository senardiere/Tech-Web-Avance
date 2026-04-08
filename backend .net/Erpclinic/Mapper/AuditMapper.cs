using Clinique.Api.DTOs;
using System.Text.Json;

namespace Clinique.Api.Mappers;

public static class AuditMapper
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
    
    private static long? GetNullableLong(JsonElement element, string propertyName)
    {
        if (element.TryGetProperty(propertyName, out var value) && value.ValueKind != JsonValueKind.Null)
        {
            return value.GetInt64();
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
    
    public static AuditLogDto ToDto(JsonElement javaEntity)
    {
        if (javaEntity.ValueKind == JsonValueKind.Null)
            return null!;
        
        return new AuditLogDto
        {
            Id = GetLong(javaEntity, "id"),
            Utilisateur = GetString(javaEntity, "utilisateur"),
            Action = GetString(javaEntity, "action"),
            Entite = GetString(javaEntity, "entite"),
            EntiteId = GetNullableLong(javaEntity, "entiteId"),
            Details = GetString(javaEntity, "details"),
            AdresseIp = GetString(javaEntity, "adresseIp"),
            DateAction = GetDateTime(javaEntity, "dateAction")
        };
    }
    
    public static List<AuditLogDto> ToDtoList(List<JsonElement> javaEntities)
    {
        if (javaEntities == null || javaEntities.Count == 0)
            return new List<AuditLogDto>();
            
        return javaEntities.Select(ToDto).ToList();
    }
}
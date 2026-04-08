using Clinique.Api.DTOs;
using System.Text.Json;

namespace Clinique.Api.Mappers;

public static class SpecialiteMapper
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
    
    private static bool GetBool(JsonElement element, string propertyName)
    {
        return element.TryGetProperty(propertyName, out var value) && value.ValueKind != JsonValueKind.Null
            ? value.GetBoolean() : false;
    }
    
    // Utiliser SpecialiteDto existant (dans MedecinDto.cs)
    public static SpecialiteDto ToDto(JsonElement javaEntity)
    {
        if (javaEntity.ValueKind == JsonValueKind.Null)
            return null!;
        
        return new SpecialiteDto
        {
            Id = GetLong(javaEntity, "id"),
            Nom = GetString(javaEntity, "nom"),
            // Code et Description peuvent ne pas exister dans SpecialiteDto original
            // Si c'est le cas, supprimez ces lignes
        };
    }
    
    public static List<SpecialiteDto> ToDtoList(List<JsonElement> javaEntities)
    {
        if (javaEntities == null || javaEntities.Count == 0)
            return new List<SpecialiteDto>();
            
        return javaEntities.Select(ToDto).ToList();
    }
}
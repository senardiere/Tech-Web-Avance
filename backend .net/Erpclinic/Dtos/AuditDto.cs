namespace Clinique.Api.DTOs;

public class AuditLogDto
{
    public long Id { get; set; }
    public string Utilisateur { get; set; } = string.Empty;
    public string Action { get; set; } = string.Empty;
    public string Entite { get; set; } = string.Empty;
    public long? EntiteId { get; set; }
    public string Details { get; set; } = string.Empty;
    public string AdresseIp { get; set; } = string.Empty;
    public DateTime DateAction { get; set; }
}

public class CreateAuditLogDto
{
    public string Action { get; set; } = string.Empty;
    public string Entite { get; set; } = string.Empty;
    public long? EntiteId { get; set; }
    public string Details { get; set; } = string.Empty;
    public string? Utilisateur { get; set; }
}
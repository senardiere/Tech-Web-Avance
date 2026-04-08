namespace Clinique.Api.DTOs;

public class SpecialiteRequestDto
{
    public long Id { get; set; }
    public string Nom { get; set; } = string.Empty;
    public string? Code { get; set; }
    public string? Description { get; set; }
    public bool Actif { get; set; }
}

public class CreateSpecialiteRequestDto
{
    public string Nom { get; set; } = string.Empty;
    public string? Code { get; set; }
    public string? Description { get; set; }
}

public class UpdateSpecialiteRequestDto
{
    public string? Nom { get; set; }
    public string? Code { get; set; }
    public string? Description { get; set; }
}
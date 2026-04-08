namespace Clinique.Api.DTOs;

public class MedecinDto
{
    public long Id { get; set; }
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Login { get; set; } = string.Empty;
    public string NumeroLicence { get; set; } = string.Empty;
    public string Cabinet { get; set; } = string.Empty;
    public List<string> JoursDisponibles { get; set; } = new();
    public bool Actif { get; set; }
    public DateTime DateCreation { get; set; }
    public SpecialiteDto? Specialite { get; set; }
}

public class SpecialiteDto
{
    public long Id { get; set; }
    public string Nom { get; set; } = string.Empty;
}

public class CreateMedecinDto
{
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Login { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
    public string NumeroLicence { get; set; } = string.Empty;
    public string Cabinet { get; set; } = string.Empty;
    public long? SpecialiteId { get; set; }
    public List<string> JoursDisponibles { get; set; } = new();
}

public class UpdateMedecinDto
{
    public string? Nom { get; set; }
    public string? Prenom { get; set; }
    public string? Email { get; set; }
    public string? Login { get; set; }
    public string? NumeroLicence { get; set; }
    public string? Cabinet { get; set; }
    public long? SpecialiteId { get; set; }
    public List<string>? JoursDisponibles { get; set; }
}
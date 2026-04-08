namespace Clinique.Api.DTOs;

public class PatientDto
{
    public long Id { get; set; }
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Telephone { get; set; } = string.Empty;
    public string Adresse { get; set; } = string.Empty;
    public DateTime? DateNaissance { get; set; }
    public string? NumeroSecuriteSociale { get; set; }
    public string? Mutuelle { get; set; }
    public string? PersonneContact { get; set; }
    public string? TelephoneContact { get; set; }
    public string Statut { get; set; } = string.Empty;
    public DateTime DateCreation { get; set; }
    public DateTime? DerniereVisite { get; set; }
    public MedecinRefDto? MedecinTraitant { get; set; }
}

public class MedecinRefDto
{
    public long Id { get; set; }
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
}

public class CreatePatientDto
{
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Telephone { get; set; } = string.Empty;
    public string Adresse { get; set; } = string.Empty;
    public DateTime? DateNaissance { get; set; }
    public string? NumeroSecuriteSociale { get; set; }
    public string? Mutuelle { get; set; }
    public string? PersonneContact { get; set; }
    public string? TelephoneContact { get; set; }
    public long? MedecinTraitantId { get; set; }
}

public class UpdatePatientDto
{
    public string? Nom { get; set; }
    public string? Prenom { get; set; }
    public string? Email { get; set; }
    public string? Telephone { get; set; }
    public string? Adresse { get; set; }
    public DateTime? DateNaissance { get; set; }
    public string? Mutuelle { get; set; }
    public string? PersonneContact { get; set; }
    public string? TelephoneContact { get; set; }
    public long? MedecinTraitantId { get; set; }
}
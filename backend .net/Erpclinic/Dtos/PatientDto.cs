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

// DTO spécifique pour l'appel à l'API Java
public class JavaPatientRequestDto
{
    public string last_name { get; set; } = string.Empty;
    public string first_name { get; set; } = string.Empty;
    public string email { get; set; } = string.Empty;
    public string telephone { get; set; } = string.Empty;
    public string adresse { get; set; } = string.Empty;
    public string? date_naissance { get; set; }
    public string? numero_securite_sociale { get; set; }
    public string? mutuelle { get; set; }
    public string? personne_contact { get; set; }
    public string? telephone_contact { get; set; }
    public long? medecin_traitant_id { get; set; }
    public string statut { get; set; } = "ACTIF";
}
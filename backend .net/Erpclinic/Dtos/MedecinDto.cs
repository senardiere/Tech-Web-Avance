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


public class JavaMedecinRequestDto
{
    public string last_name { get; set; } = string.Empty;
    public string first_name { get; set; } = string.Empty;
    public string email { get; set; } = string.Empty;
    public string login { get; set; } = string.Empty;
    public string mot_de_passe { get; set; } = string.Empty;
    public string? telephone { get; set; }
    public string? numero_licence { get; set; }
    public string? cabinet { get; set; }
    public long? specialite_id { get; set; }
    public string role { get; set; } = "MEDECIN";
    public bool actif { get; set; } = true;
}
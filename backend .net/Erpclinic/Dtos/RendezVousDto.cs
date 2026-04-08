namespace Clinique.Api.DTOs;

public class RendezVousDto
{
    public long Id { get; set; }
    public DateTime DateHeure { get; set; }
    public int Duree { get; set; }
    public string Statut { get; set; } = string.Empty;
    public string? Motif { get; set; }
    public string? MotifAnnulation { get; set; }
    public DateTime? DatePrise { get; set; }
    public DateTime? DateValidation { get; set; }
    public DateTime? DateAnnulation { get; set; }
    public RendezVousPatientDto? Patient { get; set; }
    public RendezVousMedecinDto? Medecin { get; set; }
    public RendezVousConsultationDto? Consultation { get; set; }
}

public class RendezVousPatientDto
{
    public long Id { get; set; }
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
}

public class RendezVousMedecinDto
{
    public long Id { get; set; }
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
}

public class RendezVousConsultationDto
{
    public long Id { get; set; }
}

public class CreateRendezVousDto
{
    public long PatientId { get; set; }
    public long MedecinId { get; set; }
    public DateTime DateHeure { get; set; }
    public int Duree { get; set; } = 30;
    public string? Motif { get; set; }
}

public class UpdateRendezVousDto
{
    public DateTime? DateHeure { get; set; }
    public int? Duree { get; set; }
    public string? Motif { get; set; }
}

public class AnnulerRendezVousDto
{
    public string Motif { get; set; } = string.Empty;
}
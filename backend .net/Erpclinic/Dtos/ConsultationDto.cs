namespace Clinique.Api.DTOs;

public class ConsultationDto
{
    public long Id { get; set; }
    public long PatientId { get; set; }
    public string PatientNom { get; set; } = string.Empty;
    public string PatientPrenom { get; set; } = string.Empty;
    public long MedecinId { get; set; }
    public string MedecinNom { get; set; } = string.Empty;
    public string MedecinPrenom { get; set; } = string.Empty;
    public long? RendezVousId { get; set; }
    public DateTime DateConsultation { get; set; }
    public string Statut { get; set; } = string.Empty;
    public string? Motif { get; set; }
    public string? Diagnostic { get; set; }
    public string? Prescription { get; set; }
    public double? Montant { get; set; }
}
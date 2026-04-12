using System.ComponentModel.DataAnnotations;

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
    public DateTime? DateCloture { get; set; }
    public string Statut { get; set; } = string.Empty;
    public string? Motif { get; set; }
    public string? Diagnostic { get; set; }
    public string? Observations { get; set; }
    public string? Prescription { get; set; }
    public double? Montant { get; set; }
    public double? Poids { get; set; }
    public int? Taille { get; set; }
    public string? Tension { get; set; }
}

public class CreateConsultationDto
{
    [Required]
    public long PatientId { get; set; }
    
    [Required]
    public long MedecinId { get; set; }
    
    public long? RendezVousId { get; set; }
    
    [Required]
    public string DateConsultation { get; set; } = string.Empty;
    
    public string? Motif { get; set; }
    public string? Diagnostic { get; set; }
    public string? Observations { get; set; }
    public string? Prescription { get; set; }
    public double? Montant { get; set; }
    public double? Poids { get; set; }
    public int? Taille { get; set; }
    public string? Tension { get; set; }
}

public class UpdateConsultationDto
{
    public string? Diagnostic { get; set; }
    public string? Observations { get; set; }
    public string? Prescription { get; set; }
    public double? Poids { get; set; }
    public int? Taille { get; set; }
    public string? Tension { get; set; }
}
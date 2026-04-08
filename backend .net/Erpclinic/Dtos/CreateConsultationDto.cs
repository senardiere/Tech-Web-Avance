using System.ComponentModel.DataAnnotations;

namespace Clinique.Api.DTOs;

public class CreateConsultationDto
{
    [Required]
    public long PatientId { get; set; }
    
    [Required]
    public long MedecinId { get; set; }
    
    public long? RendezVousId { get; set; }
    public string? Motif { get; set; }
    public string? Diagnostic { get; set; }
    public string? Prescription { get; set; }
    public double? Montant { get; set; }
}
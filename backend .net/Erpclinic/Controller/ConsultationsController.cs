using Microsoft.AspNetCore.Mvc;
using Clinique.Api.DTOs;
using Clinique.Api.Mappers;
using Clinique.Api.Services;
using System.Text.Json;

namespace Clinique.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ConsultationsController : ControllerBase
{
    private readonly JavaApiClient _javaClient;
    private readonly ILogger<ConsultationsController> _logger;

    public ConsultationsController(JavaApiClient javaClient, ILogger<ConsultationsController> logger)
    {
        _javaClient = javaClient;
        _logger = logger;
    }

    // GET: api/consultations
    [HttpGet]
    public async Task<ActionResult<IEnumerable<ConsultationDto>>> GetAll()
    {
        try
        {
            _logger.LogInformation("Récupération de toutes les consultations");
            var javaEntities = await _javaClient.GetAsync("consultations");
            var result = ConsultationMapper.ToDtoList(javaEntities);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la récupération des consultations");
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // GET: api/consultations/5
    [HttpGet("{id}")]
    public async Task<ActionResult<ConsultationDto>> GetById(long id)
    {
        try
        {
            _logger.LogInformation("Récupération de la consultation {Id}", id);
            var javaEntity = await _javaClient.GetByIdAsync("consultations", id);
            
            if (javaEntity.ValueKind == JsonValueKind.Null || javaEntity.ValueKind == JsonValueKind.Undefined)
                return NotFound($"Consultation {id} non trouvée");
            
            var result = ConsultationMapper.ToDto(javaEntity);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la récupération de la consultation {Id}", id);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // POST: api/consultations
    [HttpPost]
    public async Task<ActionResult<ConsultationDto>> Create([FromBody] CreateConsultationDto dto)
    {
        try
        {
            _logger.LogInformation("=== CRÉATION CONSULTATION ===");
            _logger.LogInformation("PatientId: {PatientId}", dto.PatientId);
            _logger.LogInformation("MedecinId: {MedecinId}", dto.MedecinId);
            _logger.LogInformation("DateConsultation: {DateConsultation}", dto.DateConsultation);
            
            // Validation
            if (dto.PatientId <= 0)
                return BadRequest(new { error = "Le patient est obligatoire" });
            
            if (dto.MedecinId <= 0)
                return BadRequest(new { error = "Le médecin est obligatoire" });
            
            if (string.IsNullOrWhiteSpace(dto.DateConsultation))
                return BadRequest(new { error = "La date de consultation est obligatoire" });
            
            // Construction de la requête pour l'API Java
            var javaRequest = new
            {
                patient = new { id = dto.PatientId },
                medecin = new { id = dto.MedecinId },
                dateConsultation = dto.DateConsultation,
                motif = dto.Motif ?? "",
                diagnostic = dto.Diagnostic ?? "",
                observations = dto.Observations ?? "",
                prescriptions = dto.Prescription ?? "",
                statut = "EN_COURS",
                montant = dto.Montant ?? 0,
                poids = dto.Poids,
                taille = dto.Taille,
                tension = dto.Tension ?? ""
            };
            
            _logger.LogInformation("Envoi à Java: {@JavaRequest}", javaRequest);
            
            var javaEntity = await _javaClient.PostAsync("consultations", javaRequest);
            var result = ConsultationMapper.ToDto(javaEntity);
            
            _logger.LogInformation("Consultation créée avec succès, ID: {Id}", result.Id);
            
            return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
        }
        catch (HttpRequestException ex)
        {
            _logger.LogError(ex, "Erreur de communication avec l'API Java");
            return StatusCode(502, new { error = "Service Java indisponible", detail = ex.Message });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création de la consultation");
            return BadRequest(new { error = ex.Message });
        }
    }

    // PUT: api/consultations/5
    [HttpPut("{id}")]
    public async Task<ActionResult<ConsultationDto>> Update(long id, [FromBody] UpdateConsultationDto dto)
    {
        try
        {
            _logger.LogInformation("Mise à jour de la consultation {Id}", id);
            
            var javaRequest = new Dictionary<string, object>();
            
            if (!string.IsNullOrWhiteSpace(dto.Diagnostic))
                javaRequest["diagnostic"] = dto.Diagnostic;
                
            if (!string.IsNullOrWhiteSpace(dto.Observations))
                javaRequest["observations"] = dto.Observations;
                
            if (!string.IsNullOrWhiteSpace(dto.Prescription))
                javaRequest["prescriptions"] = dto.Prescription;
                
            if (dto.Poids.HasValue)
                javaRequest["poids"] = dto.Poids.Value;
                
            if (dto.Taille.HasValue)
                javaRequest["taille"] = dto.Taille.Value;
                
            if (!string.IsNullOrWhiteSpace(dto.Tension))
                javaRequest["tension"] = dto.Tension;
            
            if (javaRequest.Count == 0)
                return BadRequest(new { error = "Aucune donnée à mettre à jour" });
            
            _logger.LogInformation("Envoi mise à jour à Java: {@JavaRequest}", javaRequest);
            
            var javaEntity = await _javaClient.PutAsync("consultations", id, javaRequest);
            var result = ConsultationMapper.ToDto(javaEntity);
            
            return Ok(result);
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Consultation {id} non trouvée");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la mise à jour de la consultation {Id}", id);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // DELETE: api/consultations/5
    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        try
        {
            _logger.LogInformation("Suppression de la consultation {Id}", id);
            await _javaClient.DeleteAsync("consultations", id);
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Consultation {id} non trouvée");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la suppression de la consultation {Id}", id);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // GET: api/consultations/patient/10
    [HttpGet("patient/{patientId}")]
    public async Task<ActionResult<IEnumerable<ConsultationDto>>> GetByPatient(long patientId)
    {
        try
        {
            _logger.LogInformation("Récupération des consultations du patient {PatientId}", patientId);
            var javaEntities = await _javaClient.GetAsync($"consultations/patient/{patientId}");
            var result = ConsultationMapper.ToDtoList(javaEntities);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la récupération des consultations du patient {PatientId}", patientId);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // GET: api/consultations/medecin/5
    [HttpGet("medecin/{medecinId}")]
    public async Task<ActionResult<IEnumerable<ConsultationDto>>> GetByMedecin(long medecinId)
    {
        try
        {
            _logger.LogInformation("Récupération des consultations du médecin {MedecinId}", medecinId);
            var javaEntities = await _javaClient.GetAsync($"consultations/medecin/{medecinId}");
            var result = ConsultationMapper.ToDtoList(javaEntities);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la récupération des consultations du médecin {MedecinId}", medecinId);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // GET: api/consultations/medecin/5/dernieres?limit=5
    [HttpGet("medecin/{medecinId}/dernieres")]
    public async Task<ActionResult<IEnumerable<ConsultationDto>>> GetDernieresByMedecin(
        long medecinId,
        [FromQuery] int limit = 10)
    {
        try
        {
            _logger.LogInformation("Récupération des {Limit} dernières consultations du médecin {MedecinId}", limit, medecinId);
            var javaEntities = await _javaClient.GetAsync($"consultations/medecin/{medecinId}/dernieres?limit={limit}");
            var result = ConsultationMapper.ToDtoList(javaEntities);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la récupération des dernières consultations du médecin {MedecinId}", medecinId);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // GET: api/consultations/stats/total
    [HttpGet("stats/total")]
    public async Task<ActionResult<long>> CountTotal()
    {
        try
        {
            _logger.LogInformation("Comptage total des consultations");
            var count = await _javaClient.GetPrimitiveAsync<long>("consultations/stats/total");
            return Ok(count);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors du comptage total des consultations");
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // GET: api/consultations/stats/medecin/5
    [HttpGet("stats/medecin/{medecinId}")]
    public async Task<ActionResult<long>> CountByMedecin(long medecinId)
    {
        try
        {
            _logger.LogInformation("Comptage des consultations du médecin {MedecinId}", medecinId);
            var count = await _javaClient.GetPrimitiveAsync<long>($"consultations/stats/medecin/{medecinId}");
            return Ok(count);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors du comptage des consultations du médecin {MedecinId}", medecinId);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }
}
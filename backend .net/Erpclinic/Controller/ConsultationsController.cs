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
            var javaEntity = await _javaClient.GetByIdAsync("consultations", id);
            
            if (javaEntity.ValueKind == JsonValueKind.Null)
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
            var javaEntity = await _javaClient.PostAsync("consultations", dto);
            var result = ConsultationMapper.ToDto(javaEntity);
            return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création de la consultation");
            return BadRequest(new { error = ex.Message });
        }
    }

    // POST: api/consultations/from-rendezvous/5
    [HttpPost("from-rendezvous/{rendezVousId}")]
    public async Task<ActionResult<ConsultationDto>> CreateFromRendezVous(long rendezVousId)
    {
        try
        {
            var javaEntity = await _javaClient.PostAsync($"consultations/from-rendezvous/{rendezVousId}", new { });
            var result = ConsultationMapper.ToDto(javaEntity);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création depuis le rendez-vous {RendezVousId}", rendezVousId);
            return BadRequest(new { error = ex.Message });
        }
    }

    // POST: api/consultations/directe
    [HttpPost("directe")]
    public async Task<ActionResult<ConsultationDto>> CreateDirecte([FromBody] CreateConsultationDto dto)
    {
        try
        {
            var javaEntity = await _javaClient.PostAsync("consultations/directe", dto);
            var result = ConsultationMapper.ToDto(javaEntity);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création directe de la consultation");
            return BadRequest(new { error = ex.Message });
        }
    }

    // PATCH: api/consultations/5/terminer
    [HttpPatch("{id}/terminer")]
    public async Task<IActionResult> Terminer(long id)
    {
        try
        {
            await _javaClient.PatchAsync("consultations", id);
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Consultation {id} non trouvée");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la terminaison de la consultation {Id}", id);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // PUT: api/consultations/5
    [HttpPut("{id}")]
    public async Task<ActionResult<ConsultationDto>> Update(long id, [FromBody] CreateConsultationDto dto)
    {
        try
        {
            var javaEntity = await _javaClient.PutAsync("consultations", id, dto);
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

    // GET: api/consultations/patient/10/medecin/5
    [HttpGet("patient/{patientId}/medecin/{medecinId}")]
    public async Task<ActionResult<IEnumerable<ConsultationDto>>> GetByPatientAndMedecin(
        long patientId,
        long medecinId)
    {
        try
        {
            var javaEntities = await _javaClient.GetAsync($"consultations/patient/{patientId}/medecin/{medecinId}");
            var result = ConsultationMapper.ToDtoList(javaEntities);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la récupération des consultations");
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // GET: api/consultations/stats/total
    [HttpGet("stats/total")]
    public async Task<ActionResult<long>> CountTotal()
    {
        try
        {
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
            var count = await _javaClient.GetPrimitiveAsync<long>($"consultations/stats/medecin/{medecinId}");
            return Ok(count);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors du comptage des consultations du médecin {MedecinId}", medecinId);
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }

    // GET: api/consultations/stats/medecin/5/periode?debut=2024-01-01&fin=2024-12-31
    [HttpGet("stats/medecin/{medecinId}/periode")]
    public async Task<ActionResult<long>> CountByMedecinAndDateBetween(
        long medecinId,
        [FromQuery] DateTime debut,
        [FromQuery] DateTime fin)
    {
        try
        {
            var url = $"consultations/stats/medecin/{medecinId}/periode?debut={debut:yyyy-MM-dd}&fin={fin:yyyy-MM-dd}";
            var count = await _javaClient.GetPrimitiveAsync<long>(url);
            return Ok(count);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors du comptage des consultations");
            return StatusCode(500, new { error = "Erreur interne du serveur" });
        }
    }
}
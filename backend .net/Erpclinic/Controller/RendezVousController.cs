using Microsoft.AspNetCore.Mvc;
using Clinique.Api.DTOs;
using Clinique.Api.Mappers;
using Clinique.Api.Services;

namespace Clinique.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class RendezVousController : ControllerBase
{
    private readonly JavaApiClient _javaClient;
    private readonly ILogger<RendezVousController> _logger;

    public RendezVousController(JavaApiClient javaClient, ILogger<RendezVousController> logger)
    {
        _javaClient = javaClient;
        _logger = logger;
    }

    // ========== GET ==========

    [HttpGet]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetAll()
    {
        var javaEntities = await _javaClient.GetAsync("rendezvous");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<RendezVousDto>> GetById(long id)
    {
        var javaEntity = await _javaClient.GetByIdAsync("rendezvous", id);
        
        if (javaEntity.ValueKind == System.Text.Json.JsonValueKind.Null)
            return NotFound($"Rendez-vous {id} non trouvé");
        
        return Ok(RendezVousMapper.ToDto(javaEntity));
    }

    [HttpGet("patient/{patientId}")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetByPatient(long patientId)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/patient/{patientId}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("medecin/{medecinId}")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetByMedecin(long medecinId)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/medecin/{medecinId}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("medecin/{medecinId}/date")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetByMedecinAndDate(
        long medecinId,
        [FromQuery] DateTime date)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/medecin/{medecinId}/date?date={date:yyyy-MM-dd}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("medecin/{medecinId}/statut/{statut}")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetByMedecinAndStatut(
        long medecinId,
        string statut)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/medecin/{medecinId}/statut/{statut}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("statut/{statut}")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetByStatut(string statut)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/statut/{statut}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("date")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetByDate([FromQuery] DateTime date)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/date?date={date:yyyy-MM-dd}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("du-jour")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetDuJour()
    {
        var javaEntities = await _javaClient.GetAsync("rendezvous/du-jour");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("medecin/{medecinId}/date-between")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetByMedecinAndDateBetween(
        long medecinId,
        [FromQuery] DateTime startDate,
        [FromQuery] DateTime endDate)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/medecin/{medecinId}/date-between?startDate={startDate:yyyy-MM-dd}&endDate={endDate:yyyy-MM-dd}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("patient/{patientId}/medecin/{medecinId}")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetByPatientAndMedecin(
        long patientId,
        long medecinId)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/patient/{patientId}/medecin/{medecinId}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("prochains/patient/{patientId}")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetProchainsByPatient(long patientId)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/prochains/patient/{patientId}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("prochains/medecin/{medecinId}")]
    public async Task<ActionResult<IEnumerable<RendezVousDto>>> GetProchainsByMedecin(
        long medecinId,
        [FromQuery] int limit = 10)
    {
        var javaEntities = await _javaClient.GetAsync($"rendezvous/prochains/medecin/{medecinId}?limit={limit}");
        return Ok(RendezVousMapper.ToDtoList(javaEntities));
    }

    [HttpGet("check-disponibilite")]
    public async Task<ActionResult<bool>> CheckDisponibilite(
        [FromQuery] long medecinId,
        [FromQuery] DateTime dateHeure,
        [FromQuery] int duree = 30)
    {
        var result = await _javaClient.GetPrimitiveAsync<bool>($"rendezvous/check-disponibilite?medecinId={medecinId}&dateHeure={dateHeure:yyyy-MM-ddTHH:mm:ss}&duree={duree}");
        return Ok(result);
    }

    // ========== POST ==========

    [HttpPost]
    public async Task<ActionResult<RendezVousDto>> Create([FromBody] CreateRendezVousDto dto)
    {
        try
        {
            var javaEntity = await _javaClient.PostAsync("rendezvous", dto);
            var result = RendezVousMapper.ToDto(javaEntity);
            return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création du rendez-vous");
            return BadRequest(new { error = ex.Message });
        }
    }

    // ========== PUT ==========

    [HttpPut("{id}")]
    public async Task<ActionResult<RendezVousDto>> Update(long id, [FromBody] UpdateRendezVousDto dto)
    {
        try
        {
            var javaEntity = await _javaClient.PutAsync("rendezvous", id, dto);
            var result = RendezVousMapper.ToDto(javaEntity);
            return Ok(result);
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Rendez-vous {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la mise à jour du rendez-vous {Id}", id);
            return BadRequest(new { error = ex.Message });
        }
    }

    // ========== PATCH ==========

    [HttpPatch("{id}/valider")]
    public async Task<IActionResult> Valider(long id)
    {
        try
        {
            await _javaClient.PatchWithActionAsync("rendezvous", id, "valider");
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Rendez-vous {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la validation du rendez-vous {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    [HttpPatch("{id}/annuler")]
    public async Task<IActionResult> Annuler(long id, [FromQuery] string motif)
    {
        if (string.IsNullOrWhiteSpace(motif))
            return BadRequest("Le motif d'annulation est requis");

        try
        {
            await _javaClient.PatchWithActionAsync("rendezvous", id, $"annuler?motif={Uri.EscapeDataString(motif)}");
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Rendez-vous {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de l'annulation du rendez-vous {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    // ========== DELETE ==========

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        try
        {
            await _javaClient.DeleteAsync("rendezvous", id);
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Rendez-vous {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la suppression du rendez-vous {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    // ========== STATISTIQUES ==========

    [HttpGet("stats/count")]
    public async Task<ActionResult<long>> Count()
    {
        var count = await _javaClient.GetPrimitiveAsync<long>("rendezvous/stats/count");
        return Ok(count);
    }

    [HttpGet("stats/count-by-statut")]
    public async Task<ActionResult<long>> CountByStatut([FromQuery] string statut)
    {
        var count = await _javaClient.GetPrimitiveAsync<long>($"rendezvous/stats/count-by-statut?statut={statut}");
        return Ok(count);
    }

    [HttpGet("stats/count-du-jour")]
    public async Task<ActionResult<long>> CountDuJour()
    {
        var count = await _javaClient.GetPrimitiveAsync<long>("rendezvous/stats/count-du-jour");
        return Ok(count);
    }

    [HttpGet("stats/count-by-medecin-date")]
    public async Task<ActionResult<long>> CountByMedecinAndDate(
        [FromQuery] long medecinId,
        [FromQuery] DateTime date)
    {
        var count = await _javaClient.GetPrimitiveAsync<long>($"rendezvous/stats/count-by-medecin-date?medecinId={medecinId}&date={date:yyyy-MM-dd}");
        return Ok(count);
    }

    [HttpGet("stats/count-by-medecin-date-between")]
    public async Task<ActionResult<long>> CountByMedecinAndDateBetween(
        [FromQuery] long medecinId,
        [FromQuery] DateTime debut,
        [FromQuery] DateTime fin)
    {
        var count = await _javaClient.GetPrimitiveAsync<long>($"rendezvous/stats/count-by-medecin-date-between?medecinId={medecinId}&debut={debut:yyyy-MM-dd}&fin={fin:yyyy-MM-dd}");
        return Ok(count);
    }

    [HttpGet("stats/count-by-medecin-week")]
    public async Task<ActionResult<long>> CountByMedecinAndWeek([FromQuery] long medecinId)
    {
        var count = await _javaClient.GetPrimitiveAsync<long>($"rendezvous/stats/count-by-medecin-week?medecinId={medecinId}");
        return Ok(count);
    }

    [HttpGet("stats/count-by-medecin-statut")]
    public async Task<ActionResult<long>> CountByMedecinAndStatut(
        [FromQuery] long medecinId,
        [FromQuery] string statut)
    {
        var count = await _javaClient.GetPrimitiveAsync<long>($"rendezvous/stats/count-by-medecin-statut?medecinId={medecinId}&statut={statut}");
        return Ok(count);
    }
}
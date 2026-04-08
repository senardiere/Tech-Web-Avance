using Microsoft.AspNetCore.Mvc;
using Clinique.Api.DTOs;
using Clinique.Api.Mappers;
using Clinique.Api.Services;

namespace Clinique.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class MedecinsController : ControllerBase
{
    private readonly JavaApiClient _javaClient;
    private readonly ILogger<MedecinsController> _logger;

    public MedecinsController(JavaApiClient javaClient, ILogger<MedecinsController> logger)
    {
        _javaClient = javaClient;
        _logger = logger;
    }

    // ========== GET ==========

    [HttpGet]
    public async Task<ActionResult<IEnumerable<MedecinDto>>> GetAll()
    {
        var javaEntities = await _javaClient.GetAsync("medecins");
        return Ok(MedecinMapper.ToDtoList(javaEntities));
    }

    [HttpGet("actifs")]
    public async Task<ActionResult<IEnumerable<MedecinDto>>> GetActifs()
    {
        var javaEntities = await _javaClient.GetAsync("medecins/actifs");
        return Ok(MedecinMapper.ToDtoList(javaEntities));
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<MedecinDto>> GetById(long id)
    {
        var javaEntity = await _javaClient.GetByIdAsync("medecins", id);
        
        if (javaEntity.ValueKind == System.Text.Json.JsonValueKind.Null)
            return NotFound($"Médecin {id} non trouvé");
        
        return Ok(MedecinMapper.ToDto(javaEntity));
    }

    [HttpGet("login/{login}")]
    public async Task<ActionResult<MedecinDto>> GetByLogin(string login)
    {
        var javaEntities = await _javaClient.GetAsync($"medecins/login/{login}");
        var result = MedecinMapper.ToDto(javaEntities.FirstOrDefault());
        
        if (result == null || result.Id == 0)
            return NotFound($"Médecin avec login '{login}' non trouvé");
        
        return Ok(result);
    }

    [HttpGet("email/{email}")]
    public async Task<ActionResult<MedecinDto>> GetByEmail(string email)
    {
        var javaEntities = await _javaClient.GetAsync($"medecins/email/{email}");
        var result = MedecinMapper.ToDto(javaEntities.FirstOrDefault());
        
        if (result == null || result.Id == 0)
            return NotFound($"Médecin avec email '{email}' non trouvé");
        
        return Ok(result);
    }

    [HttpGet("specialite/{specialiteId}")]
    public async Task<ActionResult<IEnumerable<MedecinDto>>> GetBySpecialite(long specialiteId)
    {
        var javaEntities = await _javaClient.GetAsync($"medecins/specialite/{specialiteId}");
        return Ok(MedecinMapper.ToDtoList(javaEntities));
    }

    [HttpGet("search")]
    public async Task<ActionResult<IEnumerable<MedecinDto>>> Search([FromQuery] string keyword)
    {
        if (string.IsNullOrWhiteSpace(keyword))
            return BadRequest("Le mot-clé de recherche est requis");
        
        var javaEntities = await _javaClient.GetAsync($"medecins/search?keyword={Uri.EscapeDataString(keyword)}");
        return Ok(MedecinMapper.ToDtoList(javaEntities));
    }

    [HttpGet("{id}/jours-disponibles")]
    public async Task<ActionResult<List<string>>> GetJoursDisponibles(long id)
    {
        var jours = await _javaClient.GetPrimitiveAsync<List<string>>($"medecins/{id}/jours-disponibles");
        return Ok(jours);
    }

    // ========== POST ==========

    [HttpPost]
    public async Task<ActionResult<MedecinDto>> Create([FromBody] CreateMedecinDto dto)
    {
        try
        {
            var javaEntity = await _javaClient.PostAsync("medecins", dto);
            var result = MedecinMapper.ToDto(javaEntity);
            return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création du médecin");
            return BadRequest(new { error = ex.Message });
        }
    }

    // ========== PUT ==========

    [HttpPut("{id}")]
    public async Task<ActionResult<MedecinDto>> Update(long id, [FromBody] UpdateMedecinDto dto)
    {
        try
        {
            var javaEntity = await _javaClient.PutAsync("medecins", id, dto);
            var result = MedecinMapper.ToDto(javaEntity);
            return Ok(result);
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Médecin {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la mise à jour du médecin {Id}", id);
            return BadRequest(new { error = ex.Message });
        }
    }

    [HttpPut("{id}/specialite/{specialiteId}")]
    public async Task<ActionResult<MedecinDto>> UpdateSpecialite(long id, long specialiteId)
    {
        try
        {
            var javaEntity = await _javaClient.PutAsync("medecins", id, "specialite", new { specialiteId });
            var result = MedecinMapper.ToDto(javaEntity);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la mise à jour de la spécialité du médecin {Id}", id);
            return BadRequest(new { error = ex.Message });
        }
    }

    [HttpPut("{id}/jours-disponibles")]
    public async Task<ActionResult<MedecinDto>> UpdateJoursDisponibles(long id, [FromBody] List<string> joursDisponibles)
    {
        try
        {
            var javaEntity = await _javaClient.PutAsync("medecins", id, "jours-disponibles", joursDisponibles);
            var result = MedecinMapper.ToDto(javaEntity);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la mise à jour des jours disponibles du médecin {Id}", id);
            return BadRequest(new { error = ex.Message });
        }
    }

    // ========== PATCH ==========

    [HttpPatch("{id}/activer")]
    public async Task<IActionResult> Activer(long id)
    {
        try
        {
            await _javaClient.PatchWithActionAsync("medecins", id, "activer");
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Médecin {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de l'activation du médecin {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    [HttpPatch("{id}/desactiver")]
    public async Task<IActionResult> Desactiver(long id)
    {
        try
        {
            await _javaClient.PatchWithActionAsync("medecins", id, "desactiver");
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Médecin {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la désactivation du médecin {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    // ========== DELETE ==========

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        try
        {
            await _javaClient.DeleteAsync("medecins", id);
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Médecin {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la suppression du médecin {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    // ========== STATISTIQUES ==========

    [HttpGet("stats/count")]
    public async Task<ActionResult<long>> Count()
    {
        var count = await _javaClient.GetPrimitiveAsync<long>("medecins/stats/count");
        return Ok(count);
    }

    [HttpGet("stats/count-actifs")]
    public async Task<ActionResult<long>> CountActifs()
    {
        var count = await _javaClient.GetPrimitiveAsync<long>("medecins/stats/count-actifs");
        return Ok(count);
    }

    [HttpGet("stats/count-by-specialite/{specialiteId}")]
    public async Task<ActionResult<long>> CountBySpecialite(long specialiteId)
    {
        var count = await _javaClient.GetPrimitiveAsync<long>($"medecins/stats/count-by-specialite/{specialiteId}");
        return Ok(count);
    }
}
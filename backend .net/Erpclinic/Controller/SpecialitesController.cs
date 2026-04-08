using Microsoft.AspNetCore.Mvc;
using Clinique.Api.Services;

namespace Clinique.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class SpecialitesController : ControllerBase
{
    private readonly JavaApiClient _javaClient;
    private readonly ILogger<SpecialitesController> _logger;

    public SpecialitesController(JavaApiClient javaClient, ILogger<SpecialitesController> logger)
    {
        _javaClient = javaClient;
        _logger = logger;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var result = await _javaClient.GetAsync("specialites");
        return Ok(result);
    }

    [HttpGet("actives")]
    public async Task<IActionResult> GetActives()
    {
        var result = await _javaClient.GetAsync("specialites/actives");
        return Ok(result);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(long id)
    {
        var result = await _javaClient.GetByIdAsync("specialites", id);
        
        if (result.ValueKind == System.Text.Json.JsonValueKind.Null)
            return NotFound($"Spécialité {id} non trouvée");
        
        return Ok(result);
    }

    [HttpGet("nom/{nom}")]
    public async Task<IActionResult> GetByNom(string nom)
    {
        var result = await _javaClient.GetAsync($"specialites/nom/{nom}");
        return Ok(result);
    }

    [HttpGet("code/{code}")]
    public async Task<IActionResult> GetByCode(string code)
    {
        var result = await _javaClient.GetAsync($"specialites/code/{code}");
        return Ok(result);
    }

    [HttpGet("search")]
    public async Task<IActionResult> Search([FromQuery] string keyword)
    {
        if (string.IsNullOrWhiteSpace(keyword))
            return BadRequest("Le mot-clé de recherche est requis");
        
        var result = await _javaClient.GetAsync($"specialites/search?keyword={Uri.EscapeDataString(keyword)}");
        return Ok(result);
    }

    [HttpGet("{id}/stats")]
    public async Task<IActionResult> GetStats(long id)
    {
        var result = await _javaClient.GetAsync($"specialites/{id}/stats");
        return Ok(result);
    }

    [HttpGet("{id}/medecins")]
    public async Task<IActionResult> GetMedecinsBySpecialite(long id)
    {
        var result = await _javaClient.GetAsync($"specialites/{id}/medecins");
        return Ok(result);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] object dto)
    {
        try
        {
            var result = await _javaClient.PostAsync("specialites", dto);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création de la spécialité");
            return BadRequest(new { error = ex.Message });
        }
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> Update(long id, [FromBody] object dto)
    {
        try
        {
            var result = await _javaClient.PutAsync("specialites", id, dto);
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la mise à jour de la spécialité {Id}", id);
            return BadRequest(new { error = ex.Message });
        }
    }

    [HttpPatch("{id}/activer")]
    public async Task<IActionResult> Activer(long id)
    {
        try
        {
            await _javaClient.PatchWithActionAsync("specialites", id, "activer");
            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de l'activation de la spécialité {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    [HttpPatch("{id}/desactiver")]
    public async Task<IActionResult> Desactiver(long id)
    {
        try
        {
            await _javaClient.PatchWithActionAsync("specialites", id, "desactiver");
            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la désactivation de la spécialité {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        try
        {
            await _javaClient.DeleteAsync("specialites", id);
            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la suppression de la spécialité {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    [HttpGet("stats/count")]
    public async Task<ActionResult<long>> Count()
    {
        var count = await _javaClient.GetPrimitiveAsync<long>("specialites/stats/count");
        return Ok(count);
    }

    [HttpGet("stats/count-actives")]
    public async Task<ActionResult<long>> CountActives()
    {
        var count = await _javaClient.GetPrimitiveAsync<long>("specialites/stats/count-actives");
        return Ok(count);
    }
}
using Microsoft.AspNetCore.Mvc;
using Clinique.Api.DTOs;
using Clinique.Api.Mappers;
using Clinique.Api.Services;

namespace Clinique.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuditController : ControllerBase
{
    private readonly JavaApiClient _javaClient;
    private readonly ILogger<AuditController> _logger;

    public AuditController(JavaApiClient javaClient, ILogger<AuditController> logger)
    {
        _javaClient = javaClient;
        _logger = logger;
    }

    // ========== GET ==========

    [HttpGet]
    public async Task<ActionResult<IEnumerable<AuditLogDto>>> GetAll()
    {
        var javaEntities = await _javaClient.GetAsync("audit");
        return Ok(AuditMapper.ToDtoList(javaEntities));
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<AuditLogDto>> GetById(long id)
    {
        var javaEntity = await _javaClient.GetByIdAsync("audit", id);
        
        if (javaEntity.ValueKind == System.Text.Json.JsonValueKind.Null)
            return NotFound($"Log {id} non trouvé");
        
        return Ok(AuditMapper.ToDto(javaEntity));
    }

    [HttpGet("utilisateur/{utilisateur}")]
    public async Task<ActionResult<IEnumerable<AuditLogDto>>> GetByUtilisateur(
        string utilisateur,
        [FromQuery] int limit = 100)
    {
        var javaEntities = await _javaClient.GetAsync($"audit/utilisateur/{utilisateur}?limit={limit}");
        return Ok(AuditMapper.ToDtoList(javaEntities));
    }

    [HttpGet("action/{action}")]
    public async Task<ActionResult<IEnumerable<AuditLogDto>>> GetByAction(
        string action,
        [FromQuery] int limit = 100)
    {
        var javaEntities = await _javaClient.GetAsync($"audit/action/{action}?limit={limit}");
        return Ok(AuditMapper.ToDtoList(javaEntities));
    }

    [HttpGet("entite/{entite}")]
    public async Task<ActionResult<IEnumerable<AuditLogDto>>> GetByEntite(
        string entite,
        [FromQuery] int limit = 100)
    {
        var javaEntities = await _javaClient.GetAsync($"audit/entite/{entite}?limit={limit}");
        return Ok(AuditMapper.ToDtoList(javaEntities));
    }

    [HttpGet("entite/{entite}/{entiteId}")]
    public async Task<ActionResult<IEnumerable<AuditLogDto>>> GetByEntiteAndId(
        string entite,
        long entiteId,
        [FromQuery] int limit = 100)
    {
        var javaEntities = await _javaClient.GetAsync($"audit/entite/{entite}/{entiteId}?limit={limit}");
        return Ok(AuditMapper.ToDtoList(javaEntities));
    }

    [HttpGet("date")]
    public async Task<ActionResult<IEnumerable<AuditLogDto>>> GetByDate(
        [FromQuery] DateTime debut,
        [FromQuery] DateTime fin,
        [FromQuery] int limit = 100)
    {
        var javaEntities = await _javaClient.GetAsync($"audit/date?debut={debut:yyyy-MM-ddTHH:mm:ss}&fin={fin:yyyy-MM-ddTHH:mm:ss}&limit={limit}");
        return Ok(AuditMapper.ToDtoList(javaEntities));
    }

    // ========== POST ==========

    [HttpPost("log")]
    public async Task<IActionResult> CreateLog([FromBody] CreateAuditLogDto dto)
    {
        try
        {
            await _javaClient.PostAsync("audit/log", dto);
            return Ok(new { message = "Log créé avec succès" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création du log");
            return BadRequest(new { error = ex.Message });
        }
    }

    // ========== DELETE ==========

    [HttpDelete("old")]
    public async Task<IActionResult> DeleteOldLogs()
    {
        try
        {
            var count = await _javaClient.GetPrimitiveAsync<int>("audit/old");
            return Ok(new { message = $"{count} logs supprimés" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la suppression des anciens logs");
            return StatusCode(500, new { error = ex.Message });
        }
    }

    // ========== STATISTIQUES ==========

    [HttpGet("stats/count")]
    public async Task<ActionResult<long>> Count()
    {
        var count = await _javaClient.GetPrimitiveAsync<long>("audit/stats/count");
        return Ok(count);
    }
}
using Microsoft.AspNetCore.Mvc;
using Clinique.Api.DTOs;
using Clinique.Api.Mappers;
using Clinique.Api.Services;
using System.Text.Json;

namespace Clinique.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class PatientsController : ControllerBase
{
    private readonly JavaApiClient _javaClient;
    private readonly ILogger<PatientsController> _logger;

    public PatientsController(JavaApiClient javaClient, ILogger<PatientsController> logger)
    {
        _javaClient = javaClient;
        _logger = logger;
    }

    // ========== GET ==========

    [HttpGet]
    public async Task<ActionResult<IEnumerable<PatientDto>>> GetAll()
    {
        var javaEntities = await _javaClient.GetAsync("patients");
        return Ok(PatientMapper.ToDtoList(javaEntities));
    }

    [HttpGet("actifs")]
    public async Task<ActionResult<IEnumerable<PatientDto>>> GetActifs()
    {
        var javaEntities = await _javaClient.GetAsync("patients/actifs");
        return Ok(PatientMapper.ToDtoList(javaEntities));
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<PatientDto>> GetById(long id)
    {
        var javaEntity = await _javaClient.GetByIdAsync("patients", id);
        
        if (javaEntity.ValueKind == System.Text.Json.JsonValueKind.Null)
            return NotFound($"Patient {id} non trouvé");
        
        return Ok(PatientMapper.ToDto(javaEntity));
    }

    [HttpGet("search")]
    public async Task<ActionResult<IEnumerable<PatientDto>>> Search([FromQuery] string nom)
    {
        if (string.IsNullOrWhiteSpace(nom))
            return BadRequest("Le nom est requis pour la recherche");
        
        var javaEntities = await _javaClient.GetAsync($"patients/search?nom={Uri.EscapeDataString(nom)}");
        return Ok(PatientMapper.ToDtoList(javaEntities));
    }

    [HttpGet("derniers")]
    public async Task<ActionResult<IEnumerable<PatientDto>>> GetDerniers([FromQuery] int limit = 10)
    {
        var javaEntities = await _javaClient.GetAsync($"patients/derniers?limit={limit}");
        return Ok(PatientMapper.ToDtoList(javaEntities));
    }

    [HttpGet("medecin/{medecinId}")]
    public async Task<ActionResult<IEnumerable<PatientDto>>> GetByMedecin(long medecinId)
    {
        var javaEntities = await _javaClient.GetAsync($"patients/medecin/{medecinId}");
        return Ok(PatientMapper.ToDtoList(javaEntities));
    }

    [HttpGet("medecin/{medecinId}/derniers")]
    public async Task<ActionResult<IEnumerable<PatientDto>>> GetDerniersByMedecin(long medecinId, [FromQuery] int limit = 10)
    {
        var javaEntities = await _javaClient.GetAsync($"patients/medecin/{medecinId}/derniers?limit={limit}");
        return Ok(PatientMapper.ToDtoList(javaEntities));
    }

    [HttpGet("medecin/{medecinId}/search")]
    public async Task<ActionResult<IEnumerable<PatientDto>>> SearchByMedecin(long medecinId, [FromQuery] string keyword)
    {
        if (string.IsNullOrWhiteSpace(keyword))
            return BadRequest("Le mot-clé est requis pour la recherche");
        
        var javaEntities = await _javaClient.GetAsync($"patients/medecin/{medecinId}/search?keyword={Uri.EscapeDataString(keyword)}");
        return Ok(PatientMapper.ToDtoList(javaEntities));
    }

    // ========== POST ==========

    [HttpPost]
    public async Task<ActionResult<PatientDto>> Create([FromBody] CreatePatientDto dto)
    {
        try
        {
            _logger.LogInformation("=== CRÉATION PATIENT ===");
            _logger.LogInformation("Nom reçu: {Nom}", dto.Nom);
            _logger.LogInformation("Prénom reçu: {Prenom}", dto.Prenom);
            _logger.LogInformation("Email reçu: {Email}", dto.Email);
            
            // Valider les champs obligatoires
            if (string.IsNullOrWhiteSpace(dto.Nom))
                return BadRequest(new { error = "Le nom est obligatoire" });
                
            if (string.IsNullOrWhiteSpace(dto.Prenom))
                return BadRequest(new { error = "Le prénom est obligatoire" });
            
            if (string.IsNullOrWhiteSpace(dto.Email))
                return BadRequest(new { error = "L'email est obligatoire" });
            
            if (string.IsNullOrWhiteSpace(dto.Telephone))
                return BadRequest(new { error = "Le téléphone est obligatoire" });
            
            // Créer un objet avec les noms de champs attendus par Java
            var javaRequest = new
            {
                last_name = dto.Nom,                          // ← Important: underscore
                first_name = dto.Prenom,                      // ← Important: underscore
                email = dto.Email,
                telephone = dto.Telephone,
                adresse = dto.Adresse ?? "",
                date_naissance = dto.DateNaissance?.ToString("yyyy-MM-dd"),
                numero_securite_sociale = dto.NumeroSecuriteSociale,
                mutuelle = dto.Mutuelle,
                personne_contact = dto.PersonneContact,
                telephone_contact = dto.TelephoneContact,
                medecin_traitant_id = dto.MedecinTraitantId,  // ← Important: underscore
                statut = "ACTIF"
            };
            
            _logger.LogInformation("Envoi à Java: {@JavaRequest}", javaRequest);
            
            var javaEntity = await _javaClient.PostAsync("patients", javaRequest);
            var result = PatientMapper.ToDto(javaEntity);
            
            _logger.LogInformation("Patient créé avec succès, ID: {Id}", result.Id);
            
            return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
        }
        catch (HttpRequestException ex)
        {
            _logger.LogError(ex, "Erreur de communication avec l'API Java");
            return StatusCode(502, new { error = "Service Java indisponible", detail = ex.Message });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la création du patient");
            return BadRequest(new { error = ex.Message });
        }
    }

    // ========== PUT ==========

    [HttpPut("{id}")]
    public async Task<ActionResult<PatientDto>> Update(long id, [FromBody] UpdatePatientDto dto)
    {
        try
        {
            // Créer un objet avec les noms de champs attendus par Java
            var javaRequest = new
            {
                last_name = dto.Nom,
                first_name = dto.Prenom,
                email = dto.Email,
                telephone = dto.Telephone,
                adresse = dto.Adresse,
                date_naissance = dto.DateNaissance?.ToString("yyyy-MM-dd"),
                mutuelle = dto.Mutuelle,
                personne_contact = dto.PersonneContact,
                telephone_contact = dto.TelephoneContact,
                medecin_traitant_id = dto.MedecinTraitantId
            };
            
            var javaEntity = await _javaClient.PutAsync("patients", id, javaRequest);
            var result = PatientMapper.ToDto(javaEntity);
            return Ok(result);
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Patient {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la mise à jour du patient {Id}", id);
            return BadRequest(new { error = ex.Message });
        }
    }

    [HttpPut("{id}/derniere-visite")]
    public async Task<IActionResult> UpdateDerniereVisite(long id, [FromQuery] DateTime dateVisite)
    {
        try
        {
            await _javaClient.PutAsync("patients", id, $"derniere-visite?dateVisite={dateVisite:yyyy-MM-dd}", new { });
            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la mise à jour de la dernière visite du patient {Id}", id);
            return BadRequest(new { error = ex.Message });
        }
    }

    // ========== DELETE ==========

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        try
        {
            await _javaClient.DeleteAsync("patients", id);
            return NoContent();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return NotFound($"Patient {id} non trouvé");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la suppression du patient {Id}", id);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    // ========== STATISTIQUES ==========

    [HttpGet("stats/count")]
    public async Task<ActionResult<long>> Count()
    {
        var count = await _javaClient.GetPrimitiveAsync<long>("patients/stats/count");
        return Ok(count);
    }

    [HttpGet("stats/count-by-medecin/{medecinId}")]
    public async Task<ActionResult<long>> CountByMedecin(long medecinId)
    {
        var count = await _javaClient.GetPrimitiveAsync<long>($"patients/stats/count-by-medecin/{medecinId}");
        return Ok(count);
    }

    [HttpGet("stats/count-by-medecin-date")]
    public async Task<ActionResult<long>> CountByMedecinAndDateBetween(
        [FromQuery] long medecinId,
        [FromQuery] DateTime debut,
        [FromQuery] DateTime fin)
    {
        var url = $"patients/stats/count-by-medecin-date?medecinId={medecinId}&debut={debut:yyyy-MM-dd}&fin={fin:yyyy-MM-dd}";
        var count = await _javaClient.GetPrimitiveAsync<long>(url);
        return Ok(count);
    }
}
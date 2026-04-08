using Microsoft.AspNetCore.Mvc;
using System.Text;
using System.Text.Json;

namespace Clinique.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly ILogger<AuthController> _logger;

    public AuthController(ILogger<AuthController> logger)
    {
        _logger = logger;
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest request)
    {
        try
        {
            _logger.LogInformation("=== TENTATIVE DE CONNEXION ===");
            _logger.LogInformation("Login: {Login}", request.login);
            
            using var client = new HttpClient();
            var json = JsonSerializer.Serialize(request);
            var content = new StringContent(json, Encoding.UTF8, "application/json");
            var response = await client.PostAsync("http://localhost:8081/internal/auth/login", content);
            var result = await response.Content.ReadAsStringAsync();
            
            _logger.LogInformation("Status code: {StatusCode}", response.StatusCode);
            _logger.LogInformation("Réponse: {Result}", result);
            
            if (response.IsSuccessStatusCode)
            {
                return Ok(JsonSerializer.Deserialize<object>(result));
            }
            
            return Unauthorized(new { error = "Login ou mot de passe incorrect" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la connexion");
            return Unauthorized(new { error = "Login ou mot de passe incorrect" });
        }
    }

    [HttpPost("logout")]
    public async Task<IActionResult> Logout()
    {
        try
        {
            using var client = new HttpClient();
            await client.PostAsync("http://localhost:8081/internal/auth/logout", null);
            return Ok(new { message = "Déconnecté avec succès" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur lors de la déconnexion");
            return Ok(new { message = "Déconnecté" });
        }
    }

    [HttpGet("current-user")]
    public async Task<IActionResult> GetCurrentUser()
    {
        try
        {
            using var client = new HttpClient();
            var response = await client.GetAsync("http://localhost:8081/internal/auth/current-user");
            var result = await response.Content.ReadAsStringAsync();
            return Ok(JsonSerializer.Deserialize<object>(result));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur récupération utilisateur");
            return Ok(new { authenticated = false });
        }
    }

    [HttpGet("is-authenticated")]
    public async Task<IActionResult> IsAuthenticated()
    {
        try
        {
            using var client = new HttpClient();
            var response = await client.GetAsync("http://localhost:8081/internal/auth/is-authenticated");
            var result = await response.Content.ReadAsStringAsync();
            return Ok(JsonSerializer.Deserialize<object>(result));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur vérification authentification");
            return Ok(new { authenticated = false });
        }
    }

    [HttpGet("is-admin")]
    public async Task<IActionResult> IsAdmin()
    {
        try
        {
            using var client = new HttpClient();
            var response = await client.GetAsync("http://localhost:8081/internal/auth/is-admin");
            var result = await response.Content.ReadAsStringAsync();
            return Ok(JsonSerializer.Deserialize<object>(result));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur vérification admin");
            return Ok(new { isAdmin = false });
        }
    }

    [HttpGet("is-medecin")]
    public async Task<IActionResult> IsMedecin()
    {
        try
        {
            using var client = new HttpClient();
            var response = await client.GetAsync("http://localhost:8081/internal/auth/is-medecin");
            var result = await response.Content.ReadAsStringAsync();
            return Ok(JsonSerializer.Deserialize<object>(result));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur vérification médecin");
            return Ok(new { isMedecin = false });
        }
    }

    [HttpPost("admins")]
    public async Task<IActionResult> CreateAdmin([FromBody] CreateAdminRequest request)
    {
        try
        {
            using var client = new HttpClient();
            var json = JsonSerializer.Serialize(request);
            var content = new StringContent(json, Encoding.UTF8, "application/json");
            var response = await client.PostAsync("http://localhost:8081/internal/auth/admins", content);
            var result = await response.Content.ReadAsStringAsync();
            return Ok(JsonSerializer.Deserialize<object>(result));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur création admin");
            return BadRequest(new { error = ex.Message });
        }
    }

    [HttpPost("medecins")]
    public async Task<IActionResult> CreateMedecin([FromBody] CreateMedecinRequest request)
    {
        try
        {
            using var client = new HttpClient();
            var json = JsonSerializer.Serialize(request);
            var content = new StringContent(json, Encoding.UTF8, "application/json");
            var response = await client.PostAsync("http://localhost:8081/internal/auth/medecins", content);
            var result = await response.Content.ReadAsStringAsync();
            return Ok(JsonSerializer.Deserialize<object>(result));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erreur création médecin");
            return BadRequest(new { error = ex.Message });
        }
    }
}

public class LoginRequest
{
    public string login { get; set; } = string.Empty;     
    public string motDePasse { get; set; } = string.Empty; 
}

public class CreateAdminRequest
{
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Login { get; set; } = string.Empty;
    public string MotDePasse { get; set; } = string.Empty;
    public string? Telephone { get; set; }
    public string? Departement { get; set; }
}

public class CreateMedecinRequest
{
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Login { get; set; } = string.Empty;
    public string MotDePasse { get; set; } = string.Empty;
    public string? Telephone { get; set; }
    public string? NumeroLicence { get; set; }
    public string? Cabinet { get; set; }
    public long? SpecialiteId { get; set; }
}

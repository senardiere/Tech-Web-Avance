using System.Text;
using System.Text.Json;

namespace Clinique.Api.Services;

public class JavaApiClient
{
    private readonly HttpClient _httpClient;
    private readonly ILogger<JavaApiClient> _logger;
    private readonly string _baseUrl;

    public JavaApiClient(HttpClient httpClient, ILogger<JavaApiClient> logger, IConfiguration config)
    {
        _httpClient = httpClient;
        _logger = logger;
        _baseUrl = config["JavaApi:BaseUrl"] ?? "http://localhost:8081/internal";
        // Important : Nettoyer les slashes
        _baseUrl = _baseUrl.TrimEnd('/');
    }

    public async Task<List<JsonElement>> GetAsync(string endpoint)
    {
        // Construire l'URL complète avec BaseUrl + endpoint
        var fullUrl = $"{_baseUrl}/{endpoint.TrimStart('/')}";
        _logger.LogInformation("Appel GET: {FullUrl}", fullUrl);
        
        var response = await _httpClient.GetAsync(fullUrl);
        response.EnsureSuccessStatusCode();
        var json = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<List<JsonElement>>(json) ?? new List<JsonElement>();
    }

    public async Task<JsonElement> GetByIdAsync(string endpoint, long id)
    {
        var fullUrl = $"{_baseUrl}/{endpoint.TrimStart('/')}/{id}";
        _logger.LogInformation("Appel GET by ID: {FullUrl}", fullUrl);
        
        var response = await _httpClient.GetAsync(fullUrl);
        
        if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
            return new JsonElement();
            
        response.EnsureSuccessStatusCode();
        var json = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<JsonElement>(json);
    }

    public async Task<JsonElement> PostAsync<T>(string endpoint, T data)
    {
        var fullUrl = $"{_baseUrl}/{endpoint.TrimStart('/')}";
        _logger.LogInformation("Appel POST: {FullUrl}", fullUrl);
        
        var json = JsonSerializer.Serialize(data);
        var content = new StringContent(json, Encoding.UTF8, "application/json");
        var response = await _httpClient.PostAsync(fullUrl, content);
        response.EnsureSuccessStatusCode();
        var responseJson = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<JsonElement>(responseJson);
    }

    public async Task<JsonElement> PutAsync<T>(string endpoint, long id, T data)
    {
        var fullUrl = $"{_baseUrl}/{endpoint.TrimStart('/')}/{id}";
        _logger.LogInformation("Appel PUT: {FullUrl}", fullUrl);
        
        var json = JsonSerializer.Serialize(data);
        var content = new StringContent(json, Encoding.UTF8, "application/json");
        var response = await _httpClient.PutAsync(fullUrl, content);
        response.EnsureSuccessStatusCode();
        var responseJson = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<JsonElement>(responseJson);
    }

    public async Task PatchAsync(string endpoint, long id)
    {
        var fullUrl = $"{_baseUrl}/{endpoint.TrimStart('/')}/{id}/terminer";
        _logger.LogInformation("Appel PATCH: {FullUrl}", fullUrl);
        
        var response = await _httpClient.PatchAsync(fullUrl, null);
        response.EnsureSuccessStatusCode();
    }

    public async Task DeleteAsync(string endpoint, long id)
    {
        var fullUrl = $"{_baseUrl}/{endpoint.TrimStart('/')}/{id}";
        _logger.LogInformation("Appel DELETE: {FullUrl}", fullUrl);
        
        var response = await _httpClient.DeleteAsync(fullUrl);
        response.EnsureSuccessStatusCode();
    }

    public async Task<T> GetPrimitiveAsync<T>(string endpoint)
    {
        var fullUrl = $"{_baseUrl}/{endpoint.TrimStart('/')}";
        _logger.LogInformation("Appel GET primitive: {FullUrl}", fullUrl);
        
        var response = await _httpClient.GetAsync(fullUrl);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<T>();
    }
}
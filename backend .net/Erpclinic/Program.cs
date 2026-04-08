using Clinique.Api.Services;
using System.Text;
using System.Text.Json;

var builder = WebApplication.CreateBuilder(args);

// Ajouter les controllers MVC
builder.Services.AddControllers();

// Configuration Swagger
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// HttpClient pour appeler Java
builder.Services.AddHttpClient<JavaApiClient>();

// CORS pour React (important pour la communication frontend)
builder.Services.AddCors(options =>
{
    options.AddPolicy("ReactApp", policy =>
    {
        policy.WithOrigins("http://localhost:3000")
              .AllowAnyMethod()
              .AllowAnyHeader()
              .AllowCredentials();
    });
});

var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

// Activer CORS
app.UseCors("ReactApp");

app.UseAuthorization();
app.MapControllers();

// ========== ENDPOINTS DE TEST POUR DIAGNOSTIC ==========

// Test simple pour vérifier que l'API .NET répond
app.MapGet("/ping", () => Results.Ok(new { message = "API .NET fonctionne", timestamp = DateTime.Now }));

// Test pour vérifier la communication avec Java
app.MapGet("/diag", async () =>
{
    using var client = new HttpClient();
    try
    {
        var response = await client.GetAsync("http://localhost:8081/internal/auth/is-authenticated");
        var content = await response.Content.ReadAsStringAsync();
        return Results.Ok(new { 
            javaStatus = response.StatusCode, 
            javaResponse = content,
            netStatus = "OK" 
        });
    }
    catch (Exception ex)
    {
        return Results.BadRequest(new { error = ex.Message });
    }
});

// Test de login direct (fonctionne)
app.MapPost("/test-login", async (HttpContext context) =>
{
    try
    {
        using var client = new HttpClient();
        var body = await new StreamReader(context.Request.Body).ReadToEndAsync();
        
        Console.WriteLine("=== TEST LOGIN ===");
        Console.WriteLine($"Body reçu: {body}");
        
        var content = new StringContent(body, Encoding.UTF8, "application/json");
        var response = await client.PostAsync("http://localhost:8081/internal/auth/login", content);
        var result = await response.Content.ReadAsStringAsync();
        
        Console.WriteLine($"Status code: {response.StatusCode}");
        Console.WriteLine($"Réponse: {result}");
        
        return Results.Text(result, "application/json");
    }
    catch (Exception ex)
    {
        Console.WriteLine($"Erreur: {ex.Message}");
        return Results.BadRequest(new { error = ex.Message });
    }
});

// Test de login avec la même logique que le controller
app.MapPost("/test-auth-login", async (HttpContext context) =>
{
    try
    {
        using var client = new HttpClient();
        var body = await new StreamReader(context.Request.Body).ReadToEndAsync();
        
        var content = new StringContent(body, Encoding.UTF8, "application/json");
        var response = await client.PostAsync("http://localhost:8081/internal/auth/login", content);
        var result = await response.Content.ReadAsStringAsync();
        
        if (response.IsSuccessStatusCode)
        {
            return Results.Ok(JsonSerializer.Deserialize<object>(result));
        }
        
        return Results.Unauthorized();
    }
    catch (Exception ex)
    {
        return Results.BadRequest(new { error = ex.Message });
    }
});

app.Run();
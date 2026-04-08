using Clinique.Api.Services;

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

app.Run();
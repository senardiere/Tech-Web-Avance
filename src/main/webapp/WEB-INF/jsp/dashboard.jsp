<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="includes/header.jsp"/>

<style>
    .welcome-section {
        background: linear-gradient(135deg, #3498db, #2c3e50);
        color: white;
        padding: 30px;
        border-radius: 10px;
        margin-bottom: 30px;
    }
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 20px;
        margin: 20px 0 30px 0;
    }
    .stat-card {
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        text-align: center;
    }
    .stat-card h3 {
        margin: 0 0 10px 0;
        color: #2c3e50;
        font-size: 14px;
        text-transform: uppercase;
    }
    .stat-number {
        font-size: 36px;
        font-weight: bold;
        color: #3498db;
        margin: 10px 0;
    }
    .action-card {
        background: white;
        padding: 25px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        text-align: center;
        transition: transform 0.3s;
        text-decoration: none;
        color: #2c3e50;
        display: block;
    }
    .action-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 5px 20px rgba(0,0,0,0.2);
    }
    .action-icon {
        font-size: 40px;
        margin-bottom: 10px;
    }
    .action-title {
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 5px;
    }
    .action-link {
        color: #3498db;
        font-size: 14px;
    }
    .quick-actions {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 20px;
        margin: 30px 0;
    }
</style>

<div class="container">
    <!-- Section de bienvenue -->
    <div class="welcome-section">
        <h1 style="margin: 0 0 10px 0;">👋 Bonjour ${user.prenom} ${user.nom}</h1>
        <p style="margin: 5px 0; opacity: 0.9;">Date du jour : ${dateToday}</p>
        <p style="margin: 5px 0; opacity: 0.9;">Rôle : Administrateur</p>
    </div>

    <h2 style="color: #2c3e50; margin-bottom: 20px;">📊 Statistiques générales</h2>

    <!-- Statistiques -->
    <div class="stats-grid">
        <div class="stat-card">
            <h3>Patients</h3>
            <div class="stat-number">${totalPatients}</div>
            <a href="${pageContext.request.contextPath}/patients" style="color: #3498db;">Voir tous →</a>
        </div>
        <div class="stat-card">
            <h3>Médecins</h3>
            <div class="stat-number">${totalMedecins}</div>
            <a href="${pageContext.request.contextPath}/medecins" style="color: #3498db;">Voir tous →</a>
        </div>
        <div class="stat-card">
            <h3>Rendez-vous</h3>
            <div class="stat-number">${totalRendezVous}</div>
            <a href="${pageContext.request.contextPath}/rendezvous" style="color: #3498db;">Voir tous →</a>
        </div>
    </div>

    <h2 style="color: #2c3e50; margin: 40px 0 20px 0;">⚡ Actions rapides</h2>

    <!-- Actions rapides -->
    <div class="quick-actions">
        <a href="${pageContext.request.contextPath}/patients/nouveau" class="action-card">
            <div class="action-icon">👤</div>
            <div class="action-title">Nouveau patient</div>
            <div class="action-link">Ajouter →</div>
        </a>
        <a href="${pageContext.request.contextPath}/medecins/nouveau" class="action-card">
            <div class="action-icon">👨‍⚕️</div>
            <div class="action-title">Nouveau médecin</div>
            <div class="action-link">Ajouter →</div>
        </a>
        <a href="${pageContext.request.contextPath}/rendezvous/nouveau" class="action-card">
            <div class="action-icon">📅</div>
            <div class="action-title">Nouveau rendez-vous</div>
            <div class="action-link">Ajouter →</div>
        </a>
    </div>

    <!-- Version simple de vos cartes originales -->
    <h2 style="color: #2c3e50; margin: 40px 0 20px 0;">📋 Gestion</h2>

    <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin: 30px 0;">
        <div style="background: #3498db; color: white; padding: 20px; border-radius: 5px;">
            <h3>Patients</h3>
            <p style="font-size: 24px; font-weight: bold;">${totalPatients}</p>
            <a href="${pageContext.request.contextPath}/patients" style="color: white;">Gérer les patients →</a>
        </div>

        <div style="background: #2ecc71; color: white; padding: 20px; border-radius: 5px;">
            <h3>Médecins</h3>
            <p style="font-size: 24px; font-weight: bold;">${totalMedecins}</p>
            <a href="${pageContext.request.contextPath}/medecins" style="color: white;">Gérer les médecins →</a>
        </div>

        <div style="background: #f39c12; color: white; padding: 20px; border-radius: 5px;">
            <h3>Rendez-vous</h3>
            <p style="font-size: 24px; font-weight: bold;">${totalRendezVous}</p>
            <a href="${pageContext.request.contextPath}/rendezvous" style="color: white;">Gérer les rendez-vous →</a>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp"/>
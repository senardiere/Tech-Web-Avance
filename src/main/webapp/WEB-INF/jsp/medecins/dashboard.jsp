<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../includes/header.jsp"/>

<style>
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 20px;
        margin: 20px 0;
    }
    .stat-card {
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        text-align: center;
    }
    .stat-card h3 {
        margin: 0 0 10px 0;
        color: #2c3e50;
        font-size: 14px;
        text-transform: uppercase;
    }
    .stat-card .number {
        font-size: 32px;
        font-weight: bold;
        color: #3498db;
    }
    .appointment-list {
        background: white;
        border-radius: 10px;
        padding: 20px;
        margin: 20px 0;
        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    }
    .appointment-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px;
        border-bottom: 1px solid #eee;
    }
    .appointment-item:last-child {
        border-bottom: none;
    }
    .appointment-time {
        background: #3498db;
        color: white;
        padding: 5px 10px;
        border-radius: 5px;
        font-weight: bold;
    }
    .patient-name {
        font-weight: bold;
        color: #2c3e50;
    }
    .badge {
        padding: 3px 8px;
        border-radius: 3px;
        font-size: 12px;
        font-weight: bold;
    }
    .badge-normal {
        background: #27ae60;
        color: white;
    }
    .welcome-section {
        background: linear-gradient(135deg, #3498db, #2c3e50);
        color: white;
        padding: 30px;
        border-radius: 10px;
        margin-bottom: 30px;
    }
    .info-message {
        background: #f8f9fa;
        border-left: 4px solid #3498db;
        padding: 15px;
        margin: 20px 0;
        border-radius: 5px;
        color: #2c3e50;
    }
    .date-format {
        color: #7f8c8d;
        font-size: 13px;
    }
</style>

<div class="container">
    <div class="welcome-section">
        <h1>👨‍⚕️ Bonjour Dr. ${medecin.prenom} ${medecin.nom}</h1>
        <p>Date du jour : ${dateToday}</p>
        <p>Spécialité : ${medecin.specialite.nom}</p>
    </div>

    <div class="info-message">
        <i>ℹ️</i> Pour créer un nouveau patient ou un nouveau rendez-vous, veuillez contacter l'administration.
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <h3>Mes Patients</h3>
            <div class="number">${totalPatients}</div>
        </div>
        <div class="stat-card">
            <h3>RDV aujourd'hui</h3>
            <div class="number">${totalRdvAujourdhui}</div>
        </div>
        <div class="stat-card">
            <h3>RDV cette semaine</h3>
            <div class="number">${totalRdvSemaine}</div>
        </div>
        <div class="stat-card">
            <h3>Consultations</h3>
            <div class="number">${totalConsultations}</div>
        </div>
    </div>

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
        <!-- Rendez-vous du jour -->
        <div class="appointment-list">
            <h2 style="margin-top: 0;">📅 Rendez-vous du jour</h2>
            <c:choose>
                <c:when test="${empty rdvDuJour}">
                    <p style="text-align: center; color: #7f8c8d;">Aucun rendez-vous aujourd'hui</p>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${rdvDuJour}" var="rdv">
                        <div class="appointment-item">
                            <span class="appointment-time">
                                ${rdv.dateHeure.toLocalTime()}h
                            </span>
                            <span class="patient-name">${rdv.patient.prenom} ${rdv.patient.nom}</span>
                            <span class="date-format">${rdv.motif}</span>
                            <a href="${pageContext.request.contextPath}/consultations/nouvelle?rdvId=${rdv.id}"
                               class="btn btn-success btn-sm">📋 Démarrer</a>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Prochains rendez-vous -->
        <div class="appointment-list">
            <h2 style="margin-top: 0;">📅 Prochains rendez-vous</h2>
            <c:choose>
                <c:when test="${empty prochainsRdv}">
                    <p style="text-align: center; color: #7f8c8d;">Aucun prochain rendez-vous</p>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${prochainsRdv}" var="rdv">
                        <div class="appointment-item">
                            <span class="badge badge-normal">
                                ${rdv.dateHeure.toLocalDate()} ${rdv.dateHeure.toLocalTime()}h
                            </span>
                            <span class="patient-name">${rdv.patient.prenom} ${rdv.patient.nom}</span>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Liste des patients -->
    <div class="appointment-list">
        <h2>👥 Mes patients</h2>
        <form action="${pageContext.request.contextPath}/mon-espace/rechercher-patient" method="get" style="margin-bottom: 15px;">
            <input type="text" name="keyword" placeholder="Rechercher un patient..."
                   style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
        </form>

        <c:choose>
            <c:when test="${empty derniersPatients}">
                <p style="text-align: center; color: #7f8c8d;">Aucun patient</p>
            </c:when>
            <c:otherwise>
                <table style="width: 100%;">
                    <thead>
                    <tr>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Téléphone</th>
                        <th>Dernière visite</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${derniersPatients}" var="p">
                        <tr>
                            <td>${p.nom}</td>
                            <td>${p.prenom}</td>
                            <td>${p.telephone}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty p.derniereVisite}">
                                        ${p.derniereVisite}
                                    </c:when>
                                    <c:otherwise>Jamais</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/mon-espace/patient/${p.id}"
                                   class="btn btn-sm">Voir</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
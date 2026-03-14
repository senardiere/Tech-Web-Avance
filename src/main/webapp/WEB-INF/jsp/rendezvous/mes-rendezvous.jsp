<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../includes/header.jsp"/>

<style>
  .page-header {
    background: linear-gradient(135deg, #3498db, #2c3e50);
    color: white;
    padding: 20px;
    border-radius: 10px;
    margin-bottom: 20px;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
    margin-bottom: 20px;
  }

  .stat-card {
    background: white;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    text-align: center;
  }

  .stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #3498db;
  }

  .stat-label {
    color: #7f8c8d;
    font-size: 13px;
  }

  .filters {
    background: white;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    display: flex;
    gap: 10px;
  }

  .filter-group {
    flex: 1;
  }

  .filter-group input, .filter-group select {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }

  .btn-filter {
    background: #3498db;
    color: white;
    border: none;
    padding: 8px 20px;
    border-radius: 4px;
    cursor: pointer;
  }

  .appointments-list {
    background: white;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    overflow: hidden;
  }

  .appointment-item {
    display: flex;
    align-items: center;
    padding: 15px;
    border-bottom: 1px solid #eee;
    transition: background 0.3s;
  }

  .appointment-item:hover {
    background: #f8f9fa;
  }

  .appointment-time {
    min-width: 100px;
    text-align: center;
    padding: 8px;
    background: #3498db;
    color: white;
    border-radius: 5px;
    font-weight: bold;
    margin-right: 20px;
  }

  .appointment-info {
    flex: 1;
  }

  .patient-name {
    font-size: 16px;
    font-weight: bold;
    color: #2c3e50;
    margin-bottom: 5px;
  }

  .patient-details {
    display: flex;
    gap: 15px;
    color: #7f8c8d;
    font-size: 13px;
  }

  .appointment-motif {
    color: #7f8c8d;
    font-size: 14px;
    margin-top: 5px;
  }

  .badge {
    padding: 3px 8px;
    border-radius: 3px;
    font-size: 12px;
    font-weight: bold;
    margin-left: 10px;
  }

  .badge-prevu {
    background: #f39c12;
    color: white;
  }

  .badge-valide {
    background: #27ae60;
    color: white;
  }

  .badge-annule {
    background: #e74c3c;
    color: white;
  }

  .empty-message {
    text-align: center;
    padding: 40px;
    color: #7f8c8d;
  }

  .btn-view {
    background: #3498db;
    color: white;
    padding: 5px 10px;
    text-decoration: none;
    border-radius: 3px;
    font-size: 12px;
  }

  .btn-view:hover {
    background: #2980b9;
  }
</style>

<div class="container">
  <div class="page-header">
    <h1 style="margin:0;">📅 Mes rendez-vous</h1>
    <p style="margin:10px 0 0; opacity:0.9;">Dr. ${medecin.prenom} ${medecin.nom}</p>
  </div>

  <!-- Statistiques -->
  <div class="stats-grid">
    <div class="stat-card">
      <div class="stat-value">${totalRdvAujourdhui}</div>
      <div class="stat-label">Aujourd'hui</div>
    </div>
    <div class="stat-card">
      <div class="stat-value">${totalRdvSemaine}</div>
      <div class="stat-label">Cette semaine</div>
    </div>
    <div class="stat-card">
      <div class="stat-value">${totalRdvMois}</div>
      <div class="stat-label">Ce mois</div>
    </div>
  </div>

  <!-- Filtres (affichage seulement) -->
  <div class="filters">
    <div class="filter-group">
      <input type="date" id="dateFilter" placeholder="Filtrer par date">
    </div>
    <div class="filter-group">
      <select id="statutFilter">
        <option value="">Tous les statuts</option>
        <option value="PREVU">Prévus</option>
        <option value="VALIDE">Validés</option>
        <option value="ANNULE">Annulés</option>
      </select>
    </div>
    <button class="btn-filter" onclick="appliquerFiltres()">Filtrer</button>
  </div>

  <!-- Liste des rendez-vous -->
  <div class="appointments-list">
    <c:choose>
      <c:when test="${empty rendezVous}">
        <div class="empty-message">
          <p style="font-size: 18px;">📅 Aucun rendez-vous</p>
          <p style="color: #95a5a6;">Vous n'avez pas de rendez-vous pour le moment</p>
        </div>
      </c:when>
      <c:otherwise>
        <c:forEach items="${rendezVous}" var="r">
          <div class="appointment-item">
            <div class="appointment-time">
              <div><fmt:formatDate value="${r.dateHeure}" pattern="dd/MM"/></div>
              <div><fmt:formatDate value="${r.dateHeure}" pattern="HH:mm"/></div>
            </div>

            <div class="appointment-info">
              <div class="patient-name">
                  ${r.patient.prenom} ${r.patient.nom}
                <span class="badge
                                    <c:choose>
                                        <c:when test="${r.statut == 'PREVU'}">badge-prevu</c:when>
                                        <c:when test="${r.statut == 'VALIDE'}">badge-valide</c:when>
                                        <c:when test="${r.statut == 'ANNULE'}">badge-annule</c:when>
                                    </c:choose>">
                    ${r.statut}
                </span>
              </div>

              <div class="patient-details">
                <span>📞 ${r.patient.telephone}</span>
                <c:if test="${not empty r.patient.email}">
                  <span>✉️ ${r.patient.email}</span>
                </c:if>
              </div>

              <div class="appointment-motif">
                <strong>Motif:</strong> ${r.motif}
              </div>
            </div>

            <div style="margin-left: 15px;">
              <a href="${pageContext.request.contextPath}/mon-espace/patient/${r.patient.id}"
                 class="btn-view">👤 Voir patient</a>
            </div>
          </div>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<script>
  function appliquerFiltres() {
    const date = document.getElementById('dateFilter').value;
    const statut = document.getElementById('statutFilter').value;

    let url = '${pageContext.request.contextPath}/mon-espace/mes-rendezvous?';
    if (date) url += 'date=' + date + '&';
    if (statut) url += 'statut=' + statut;

    window.location.href = url;
  }
</script>

<jsp:include page="../includes/footer.jsp"/>
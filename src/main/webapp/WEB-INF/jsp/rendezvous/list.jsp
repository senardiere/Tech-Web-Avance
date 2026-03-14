<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
  <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
  .filters {
    background: white;
    padding: 20px;
    border-radius: 10px;
    margin-bottom: 20px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
  }

  .filter-group {
    flex: 1;
    min-width: 200px;
  }

  .filter-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
    color: #2c3e50;
  }

  .filter-group select, .filter-group input {
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
    align-self: flex-end;
  }

  .btn-filter:hover {
    background: #2980b9;
  }

  .stats-bar {
    background: white;
    padding: 15px;
    border-radius: 10px;
    margin-bottom: 20px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .stat-item {
    text-align: center;
  }

  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #3498db;
  }

  .stat-label {
    color: #7f8c8d;
    font-size: 12px;
  }

  .table-container {
    background: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    overflow-x: auto;
  }

  table {
    width: 100%;
    border-collapse: collapse;
  }

  th {
    background: #34495e;
    color: white;
    padding: 12px;
    text-align: left;
  }

  td {
    padding: 12px;
    border-bottom: 1px solid #ddd;
  }

  tr:hover {
    background: #f5f5f5;
  }

  .badge {
    padding: 3px 8px;
    border-radius: 3px;
    font-size: 12px;
    font-weight: bold;
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

  .actions {
    display: flex;
    gap: 5px;
  }

  .btn-icon {
    padding: 5px 10px;
    border: none;
    border-radius: 3px;
    cursor: pointer;
    color: white;
    text-decoration: none;
    font-size: 12px;
  }

  .btn-view {
    background: #3498db;
  }

  .btn-edit {
    background: #f39c12;
  }

  .btn-cancel {
    background: #e74c3c;
  }

  .btn-validate {
    background: #27ae60;
  }

  .btn-delete {
    background: #c0392b;
  }

  .btn-add {
    background: #27ae60;
    color: white;
    padding: 10px 20px;
    text-decoration: none;
    border-radius: 5px;
    display: inline-block;
    margin-bottom: 15px;
  }

  .btn-add:hover {
    background: #229954;
  }

  .header-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .empty-message {
    text-align: center;
    padding: 40px;
    color: #7f8c8d;
    font-size: 16px;
  }
</style>

<div class="container">
  <div class="header-actions">
    <h2>📅 Gestion des rendez-vous</h2>
    <a href="${pageContext.request.contextPath}/rendezvous/nouveau" class="btn-add">➕ Nouveau rendez-vous</a>
  </div>

  <!-- Filtres -->
  <div class="filters">
    <div class="filter-group">
      <label>Médecin</label>
      <select id="medecinFilter">
        <option value="">Tous les médecins</option>
        <c:forEach items="${medecins}" var="m">
          <option value="${m.id}">Dr. ${m.prenom} ${m.nom}</option>
        </c:forEach>
      </select>
    </div>
    <div class="filter-group">
      <label>Statut</label>
      <select id="statutFilter">
        <option value="">Tous les statuts</option>
        <option value="PREVU">Prévu</option>
        <option value="VALIDE">Validé</option>
        <option value="ANNULE">Annulé</option>
      </select>
    </div>
    <div class="filter-group">
      <label>Date</label>
      <input type="date" id="dateFilter">
    </div>
    <button class="btn-filter" onclick="appliquerFiltres()">Filtrer</button>
  </div>

  <!-- Statistiques -->
  <div class="stats-bar">
    <div class="stat-item">
      <div class="stat-value">${totalRendezVous}</div>
      <div class="stat-label">Total</div>
    </div>
    <div class="stat-item">
      <div class="stat-value" style="color: #f39c12;">${totalPrevu}</div>
      <div class="stat-label">Prévus</div>
    </div>
    <div class="stat-item">
      <div class="stat-value" style="color: #27ae60;">${totalValide}</div>
      <div class="stat-label">Validés</div>
    </div>
    <div class="stat-item">
      <div class="stat-value" style="color: #e74c3c;">${totalAnnule}</div>
      <div class="stat-label">Annulés</div>
    </div>
    <div class="stat-item">
      <div class="stat-value">${rdvAujourdhui}</div>
      <div class="stat-label">Aujourd'hui</div>
    </div>
  </div>

  <!-- Tableau des rendez-vous -->
  <div class="table-container">
    <table>
      <thead>
      <tr>
        <th>Date</th>
        <th>Heure</th>
        <th>Patient</th>
        <th>Médecin</th>
        <th>Motif</th>
        <th>Statut</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <c:choose>
        <c:when test="${empty rendezvous}">
          <tr>
            <td colspan="7" class="empty-message">Aucun rendez-vous trouvé</td>
          </tr>
        </c:when>
        <c:otherwise>
          <c:forEach items="${rendezvous}" var="r">
            <tr>
                <%-- Formatage manuel des dates sans fmt:formatDate --%>
              <td>${r.dateHeure.toLocalDate()}</td>
              <td>${r.dateHeure.toLocalTime()}</td>
              <td>${r.patient.prenom} ${r.patient.nom}</td>
              <td>Dr. ${r.medecin.prenom} ${r.medecin.nom}</td>
              <td>${r.motif}</td>
              <td>
                                    <span class="badge
                                        <c:choose>
                                            <c:when test="${r.statut == 'PREVU'}">badge-prevu</c:when>
                                            <c:when test="${r.statut == 'VALIDE'}">badge-valide</c:when>
                                            <c:when test="${r.statut == 'ANNULE'}">badge-annule</c:when>
                                        </c:choose>">
                                        ${r.statut}
                                    </span>
              </td>
              <td class="actions">
                <a href="${pageContext.request.contextPath}/rendezvous/${r.id}" class="btn-icon btn-view" title="Voir">👁️</a>
                <c:if test="${r.statut == 'PREVU'}">
                  <a href="${pageContext.request.contextPath}/rendezvous/valider/${r.id}"
                     class="btn-icon btn-validate"
                     title="Valider"
                     onclick="return confirm('Valider ce rendez-vous ?')">✓</a>
                  <a href="${pageContext.request.contextPath}/rendezvous/annuler/${r.id}"
                     class="btn-icon btn-cancel"
                     title="Annuler">✗</a>
                </c:if>
                <c:if test="${r.statut == 'ANNULE' || r.statut == 'VALIDE'}">
                  <a href="${pageContext.request.contextPath}/rendezvous/delete/${r.id}"
                     class="btn-icon btn-delete"
                     title="Supprimer"
                     onclick="return confirm('Supprimer définitivement ce rendez-vous ?')">🗑️</a>
                </c:if>
              </td>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
      </tbody>
    </table>
  </div>
</div>

<script>
  function appliquerFiltres() {
    const medecin = document.getElementById('medecinFilter').value;
    const statut = document.getElementById('statutFilter').value;
    const date = document.getElementById('dateFilter').value;

    let url = '${pageContext.request.contextPath}/rendezvous?';
    if (medecin) url += 'medecin=' + medecin + '&';
    if (statut) url += 'statut=' + statut + '&';
    if (date) url += 'date=' + date + '&';

    window.location.href = url;
  }
</script>

<jsp:include page="../includes/footer.jsp"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
  <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
  .consult-list-wrapper {
    margin-bottom: 30px;
  }

  .consult-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
  }

  .consult-list-header h2 {
    margin: 0;
    color: #2c3e50;
  }

  .consult-table-card {
    background: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  }

  .consult-table {
    width: 100%;
    border-collapse: collapse;
  }

  .consult-table th,
  .consult-table td {
    padding: 10px 12px;
    border-bottom: 1px solid #ecf0f1;
    font-size: 14px;
  }

  .consult-table th {
    background: #f4f6f7;
    color: #2c3e50;
    font-weight: 600;
    text-align: left;
  }

  .consult-table tr:hover {
    background: #f8f9f9;
  }

  .badge-consult {
    padding: 3px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 600;
  }

  .badge-en-cours {
    background: #fff3cd;
    color: #856404;
  }

  .badge-terminee {
    background: #d4edda;
    color: #155724;
  }

  .consult-actions .btn {
    font-size: 12px;
    padding: 6px 10px;
  }
</style>

<div class="consult-list-wrapper">
  <div class="consult-list-header">
    <h2>📋 Liste des consultations</h2>
  </div>

  <div class="consult-table-card">
    <table class="consult-table">
      <thead>
      <tr>
        <th>Date</th>
        <th>Patient</th>
        <th>Médecin</th>
        <th>Diagnostic</th>
        <th>Statut</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${consultations}" var="c">
        <tr>
          <td>
            <c:choose>
              <c:when test="${not empty c.dateConsultation}">
                ${c.dateConsultation}
              </c:when>
              <c:otherwise>
                <span style="color:#999;">Non renseignée</span>
              </c:otherwise>
            </c:choose>
          </td>
          <td>${c.patient.prenom} ${c.patient.nom}</td>
          <td>Dr. ${c.medecin.prenom} ${c.medecin.nom}</td>
          <td>
            <c:choose>
              <c:when test="${empty c.diagnostic}">
                <span style="color: #999;">Non renseigné</span>
              </c:when>
              <c:otherwise>
                ${c.diagnostic.length() > 50 ? c.diagnostic.substring(0,50).concat('...') : c.diagnostic}
              </c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:choose>
              <c:when test="${c.statut == 'EN_COURS'}">
                <span class="badge-consult badge-en-cours">En cours</span>
              </c:when>
              <c:when test="${c.statut == 'TERMINEE'}">
                <span class="badge-consult badge-terminee">Terminée</span>
              </c:when>
            </c:choose>
          </td>
          <td class="consult-actions">
            <a href="${pageContext.request.contextPath}/consultations/${c.id}" class="btn">Voir</a>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
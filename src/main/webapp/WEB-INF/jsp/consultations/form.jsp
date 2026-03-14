<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
  .consult-form-wrapper {
    max-width: 900px;
    margin: 0 auto 30px auto;
  }

  .consult-form-card {
    background: white;
    padding: 25px;
    border-radius: 12px;
    box-shadow: 0 2px 15px rgba(0,0,0,0.08);
  }

  .consult-form-title {
    color: #2c3e50;
    margin-bottom: 20px;
    font-size: 22px;
    border-bottom: 2px solid #3498db;
    padding-bottom: 10px;
  }

  .consult-section {
    background: #f8f9fa;
    padding: 15px 18px;
    border-radius: 8px;
    margin-bottom: 18px;
    border-left: 4px solid #3498db;
  }

  .consult-section h4 {
    margin: 0 0 12px 0;
    color: #2c3e50;
    font-size: 15px;
  }

  .consult-form-group {
    margin-bottom: 12px;
  }

  .consult-form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 600;
    font-size: 13px;
    color: #2c3e50;
  }

  .consult-form-group textarea,
  .consult-form-group input {
    width: 100%;
    padding: 8px 10px;
    border-radius: 6px;
    border: 1px solid #d7dbdd;
    font-size: 14px;
  }

  .consult-grid-3 {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 16px;
  }
</style>

<div class="consult-form-wrapper">
  <div class="consult-form-card">
    <h2 class="consult-form-title">
      <c:choose>
        <c:when test="${empty consultation.id}">Nouvelle consultation</c:when>
        <c:otherwise>Modifier consultation</c:otherwise>
      </c:choose>
    </h2>

    <form:form method="post"
               action="${pageContext.request.contextPath}/consultations/save"
               modelAttribute="consultation">

      <form:hidden path="id"/>
      <form:hidden path="rendezVous.id"/>
      <form:hidden path="patient.id"/>
      <form:hidden path="medecin.id"/>

      <c:if test="${not empty consultation.rendezVous}">
        <div class="consult-section" style="background:#e8f4f8;">
          <h4>📅 Rendez-vous associé</h4>
          <p>
            <strong>Date :</strong> ${consultation.rendezVous.dateHeure}<br>
            <strong>Patient :</strong> ${consultation.rendezVous.patient.prenom} ${consultation.rendezVous.patient.nom}
          </p>
        </div>
      </c:if>

      <div class="consult-section">
        <h4>🩺 Diagnostic</h4>
        <div class="consult-form-group">
          <label>Diagnostic</label>
          <form:textarea path="diagnostic" rows="5"/>
        </div>
      </div>

      <div class="consult-section">
        <h4>💊 Prescriptions & Observations</h4>
        <div class="consult-form-group">
          <label>Prescriptions</label>
          <form:textarea path="prescriptions" rows="4"/>
        </div>
        <div class="consult-form-group">
          <label>Observations</label>
          <form:textarea path="observations" rows="3"/>
        </div>
      </div>

      <div class="consult-section">
        <h4>⚙️ Paramètres cliniques</h4>
        <div class="consult-grid-3">
          <div class="consult-form-group">
            <label>Poids (kg)</label>
            <form:input path="poids" type="number" step="0.1"/>
          </div>
          <div class="consult-form-group">
            <label>Taille (cm)</label>
            <form:input path="taille" type="number" step="0.5"/>
          </div>
          <div class="consult-form-group">
            <label>Tension</label>
            <form:input path="tension"/>
          </div>
        </div>
      </div>

      <div style="display:flex; gap:10px; justify-content:flex-end; margin-top:15px;">
        <a href="${pageContext.request.contextPath}/consultations" class="btn">Annuler</a>
        <button type="submit" class="btn btn-success">Enregistrer</button>
      </div>
    </form:form>
  </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
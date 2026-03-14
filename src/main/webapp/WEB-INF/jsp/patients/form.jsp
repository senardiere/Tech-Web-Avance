<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
  <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
  .form-container {
    background: white;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 2px 15px rgba(0,0,0,0.1);
    max-width: 800px;
    margin: 20px auto;
  }

  .form-title {
    color: #2c3e50;
    margin-bottom: 30px;
    text-align: center;
    font-size: 24px;
    border-bottom: 2px solid #3498db;
    padding-bottom: 15px;
  }

  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    margin-bottom: 20px;
  }

  .form-group {
    margin-bottom: 20px;
  }

  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #2c3e50;
    font-size: 14px;
  }

  .form-group label .required {
    color: #e74c3c;
    margin-left: 3px;
  }

  .form-control {
    width: 100%;
    padding: 12px;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    font-size: 14px;
    transition: border-color 0.3s;
  }

  .form-control:focus {
    border-color: #3498db;
    outline: none;
    box-shadow: 0 0 0 3px rgba(52,152,219,0.1);
  }

  .form-control.error {
    border-color: #e74c3c;
  }

  .error-message {
    color: #e74c3c;
    font-size: 12px;
    margin-top: 5px;
  }

  .btn-submit {
    background: #27ae60;
    color: white;
    padding: 14px 30px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 16px;
    font-weight: 600;
    width: 100%;
    transition: background 0.3s;
  }

  .btn-submit:hover {
    background: #229954;
  }

  .btn-cancel {
    background: #95a5a6;
    color: white;
    padding: 14px 30px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 16px;
    font-weight: 600;
    text-decoration: none;
    display: inline-block;
    text-align: center;
    width: 100%;
    transition: background 0.3s;
  }

  .btn-cancel:hover {
    background: #7f8c8d;
    color: white;
    text-decoration: none;
  }

  .info-section {
    background: #f8f9fa;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
    border-left: 4px solid #3498db;
  }

  .info-section h4 {
    margin: 0 0 10px 0;
    color: #2c3e50;
    font-size: 16px;
  }

  select.form-control {
    appearance: none;
    background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 12px center;
    background-size: 16px;
    padding-right: 40px;
  }
</style>

<div class="container">
  <div class="form-container">
    <h2 class="form-title">
      <c:choose>
        <c:when test="${empty patient.id}">
          ➕ Nouveau Patient
        </c:when>
        <c:otherwise>
          ✏️ Modifier Patient
        </c:otherwise>
      </c:choose>
    </h2>

    <form:form method="post"
               action="${pageContext.request.contextPath}/patients/save"
               modelAttribute="patient">

      <form:hidden path="id"/>

      <!-- Section: Médecin traitant (AJOUT IMPORTANT) -->
      <div class="info-section">
        <h4>👨‍⚕️ Médecin traitant</h4>
        <div class="form-group">
          <label>Sélectionner le médecin <span class="required">*</span></label>
          <form:select path="medecinTraitant.id" class="form-control" required="true">
            <option value="">-- Choisir un médecin --</option>
            <c:forEach items="${medecins}" var="m">
              <option value="${m.id}"
                ${patient.medecinTraitant.id == m.id ? 'selected' : ''}>
                Dr. ${m.prenom} ${m.nom} - ${m.specialite.nom}
              </option>
            </c:forEach>
          </form:select>
          <form:errors path="medecinTraitant" cssClass="error-message"/>
        </div>
      </div>

      <!-- Section: Identité -->
      <div class="info-section">
        <h4>👤 Identité</h4>
        <div class="form-row">
          <div class="form-group">
            <label>Nom <span class="required">*</span></label>
            <form:input path="nom" class="form-control" required="true"/>
            <form:errors path="nom" cssClass="error-message"/>
          </div>

          <div class="form-group">
            <label>Prénom <span class="required">*</span></label>
            <form:input path="prenom" class="form-control" required="true"/>
            <form:errors path="prenom" cssClass="error-message"/>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Date de naissance <span class="required">*</span></label>
            <form:input path="dateNaissance" type="date" class="form-control" required="true"/>
            <form:errors path="dateNaissance" cssClass="error-message"/>
          </div>

          <div class="form-group">
            <label>Numéro Sécurité Sociale <span class="required">*</span></label>
            <form:input path="numeroSecuriteSociale" class="form-control" required="true"/>
            <form:errors path="numeroSecuriteSociale" cssClass="error-message"/>
          </div>
        </div>
      </div>

      <!-- Section: Contact -->
      <div class="info-section">
        <h4>📞 Contact</h4>
        <div class="form-row">
          <div class="form-group">
            <label>Email <span class="required">*</span></label>
            <form:input path="email" type="email" class="form-control" required="true"/>
            <form:errors path="email" cssClass="error-message"/>
          </div>

          <div class="form-group">
            <label>Téléphone <span class="required">*</span></label>
            <form:input path="telephone" class="form-control" required="true"/>
            <form:errors path="telephone" cssClass="error-message"/>
          </div>
        </div>

        <div class="form-group">
          <label>Adresse</label>
          <form:input path="adresse" class="form-control"/>
        </div>
      </div>

      <!-- Section: Informations médicales -->
      <div class="info-section">
        <h4>🏥 Informations médicales</h4>
        <div class="form-row">
          <div class="form-group">
            <label>Mutuelle</label>
            <form:input path="mutuelle" class="form-control"/>
          </div>

          <div class="form-group">
            <label>Personne à contacter</label>
            <form:input path="personneContact" class="form-control"/>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Téléphone contact</label>
            <form:input path="telephoneContact" class="form-control"/>
          </div>

          <div class="form-group">
            <label>Statut</label>
            <form:select path="statut" class="form-control">
              <form:option value="ACTIF">Actif</form:option>
              <form:option value="INACTIF">Inactif</form:option>
            </form:select>
          </div>
        </div>
      </div>

      <!-- Boutons d'action -->
      <div class="form-row" style="margin-top: 30px;">
        <div>
          <button type="submit" class="btn-submit">
            <c:choose>
              <c:when test="${empty patient.id}">
                ➕ Créer le patient
              </c:when>
              <c:otherwise>
                💾 Enregistrer les modifications
              </c:otherwise>
            </c:choose>
          </button>
        </div>
        <div>
          <a href="${pageContext.request.contextPath}/patients" class="btn-cancel">❌ Annuler</a>
        </div>
      </div>
    </form:form>
  </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
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
    transition: background 0.3s;
  }

  .btn-cancel:hover {
    background: #7f8c8d;
    color: white;
    text-decoration: none;
  }

  .info-section {
    background: #f8f9fa;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 25px;
    border-left: 4px solid #3498db;
  }

  .info-section h4 {
    margin: 0 0 15px 0;
    color: #2c3e50;
    font-size: 16px;
  }

  .availability-status {
    margin-top: 15px;
    padding: 12px;
    border-radius: 8px;
    font-weight: 500;
    display: none;
  }

  .availability-available {
    background: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
    display: block;
  }

  .availability-unavailable {
    background: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
    display: block;
  }

  .availability-checking {
    background: #fff3cd;
    color: #856404;
    border: 1px solid #ffeeba;
    display: block;
  }
</style>

<div class="container">
  <div class="form-container">
    <h2 class="form-title">📅 Nouveau Rendez-vous</h2>

    <form:form method="post"
               action="${pageContext.request.contextPath}/rendezvous/save"
               modelAttribute="rendezVous"
               id="rendezVousForm">

      <!-- Section Patient -->
      <div class="info-section">
        <h4>👤 Patient</h4>
        <div class="form-group">
          <label>Sélectionner un patient <span class="required">*</span></label>
          <form:select path="patient.id" class="form-control" required="true" id="patientSelect">
            <form:option value="">-- Choisir un patient --</form:option>
            <c:forEach items="${patients}" var="p">
              <form:option value="${p.id}">
                ${p.prenom} ${p.nom} - ${p.telephone}
              </form:option>
            </c:forEach>
          </form:select>
          <form:errors path="patient" cssClass="error-message"/>
        </div>
      </div>

      <!-- Section Médecin -->
      <div class="info-section">
        <h4>👨‍⚕️ Médecin</h4>
        <div class="form-group">
          <label>Sélectionner un médecin <span class="required">*</span></label>
          <form:select path="medecin.id" class="form-control" required="true" id="medecinSelect">
            <form:option value="">-- Choisir un médecin --</form:option>
            <c:forEach items="${medecins}" var="m">
              <form:option value="${m.id}">
                Dr. ${m.prenom} ${m.nom} - ${m.specialite.nom}
              </form:option>
            </c:forEach>
          </form:select>
          <form:errors path="medecin" cssClass="error-message"/>
        </div>
      </div>

      <!-- Section Date et Heure -->
      <div class="info-section">
        <h4>📅 Date et Heure</h4>
        <div class="form-row">
          <div class="form-group">
            <label>Date <span class="required">*</span></label>
            <input type="date" name="date" class="form-control" required
                   value="${param.date != null ? param.date : rendezVous.dateHeure != null ? rendezVous.dateHeure.toLocalDate() : ''}"
                   id="datePicker" min="${today}">
          </div>
          <div class="form-group">
            <label>Heure <span class="required">*</span></label>
            <input type="time" name="heure" class="form-control" required
                   value="${param.heure != null ? param.heure : rendezVous.dateHeure != null ? rendezVous.dateHeure.toLocalTime() : ''}"
                   id="timePicker" step="900">
          </div>
        </div>

        <!-- Status de disponibilité -->
        <div id="availabilityStatus" class="availability-status"></div>
      </div>

      <!-- Section Détails -->
      <div class="info-section">
        <h4>📋 Détails</h4>
        <div class="form-group">
          <label>Durée (minutes)</label>
          <select name="duree" class="form-control">
            <option value="15">15 minutes</option>
            <option value="30" selected>30 minutes</option>
            <option value="45">45 minutes</option>
            <option value="60">1 heure</option>
          </select>
        </div>

        <div class="form-group">
          <label>Motif du rendez-vous <span class="required">*</span></label>
          <input type="text" name="motif" class="form-control" required
                 value="${rendezVous.motif}"
                 placeholder="Ex: Consultation de routine, Suivi, Urgence...">
        </div>
      </div>

      <!-- Boutons d'action -->
      <div class="form-row">
        <div>
          <button type="submit" class="btn-submit" id="submitBtn">📅 Créer le rendez-vous</button>
        </div>
        <div>
          <a href="${pageContext.request.contextPath}/rendezvous" class="btn-cancel">❌ Annuler</a>
        </div>
      </div>
    </form:form>
  </div>
</div>

<script>
  const medecinSelect = document.getElementById('medecinSelect');
  const datePicker = document.getElementById('datePicker');
  const timePicker = document.getElementById('timePicker');
  const availabilityStatus = document.getElementById('availabilityStatus');
  const submitBtn = document.getElementById('submitBtn');

  let checkTimeout;

  function checkDisponibilite() {
    const medecinId = medecinSelect.value;
    const date = datePicker.value;
    const time = timePicker.value;

    if (!medecinId || !date || !time) {
      availabilityStatus.style.display = 'none';
      submitBtn.disabled = false;
      return;
    }

    availabilityStatus.className = 'availability-status availability-checking';
    availabilityStatus.innerHTML = '⏳ Vérification de la disponibilité...';
    availabilityStatus.style.display = 'block';
    submitBtn.disabled = true;

    if (checkTimeout) clearTimeout(checkTimeout);

    checkTimeout = setTimeout(() => {
      fetch('${pageContext.request.contextPath}/rendezvous/verifier-disponibilite?medecinId=' + medecinId + '&date=' + date + '&heure=' + time)
              .then(response => response.json())
              .then(data => {
                if (data.disponible) {
                  availabilityStatus.className = 'availability-status availability-available';
                  availabilityStatus.innerHTML = '✅ ' + data.message;
                  submitBtn.disabled = false;
                } else {
                  availabilityStatus.className = 'availability-status availability-unavailable';
                  availabilityStatus.innerHTML = '❌ ' + data.message;
                  submitBtn.disabled = true;
                }
              })
              .catch(() => {
                availabilityStatus.className = 'availability-status availability-unavailable';
                availabilityStatus.innerHTML = '❌ Erreur de vérification';
                submitBtn.disabled = true;
              });
    }, 500);
  }

  medecinSelect.addEventListener('change', checkDisponibilite);
  datePicker.addEventListener('change', checkDisponibilite);
  timePicker.addEventListener('change', checkDisponibilite);

  document.getElementById('rendezVousForm').addEventListener('submit', function(e) {
    if (!datePicker.value || !timePicker.value) {
      e.preventDefault();
      alert('Veuillez sélectionner une date et une heure');
    }
  });
</script>

<jsp:include page="../includes/footer.jsp"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
  <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
  .specialite-form-wrapper {
    max-width: 600px;
    margin: 0 auto 30px auto;
  }

  .specialite-form-card {
    background: white;
    padding: 25px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  }

  .specialite-form-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .specialite-form-header h2 {
    margin: 0;
    color: #2c3e50;
  }

  .btn-secondary {
    background: #7f8c8d;
    color: white;
  }

  .btn-secondary:hover {
    background: #707b7c;
  }

  .form-group {
    margin-bottom: 15px;
  }

  .form-group label {
    display: block;
    font-weight: 600;
    margin-bottom: 5px;
    color: #2c3e50;
  }

  .form-group input,
  .form-group textarea {
    width: 100%;
    padding: 8px 10px;
    border-radius: 4px;
    border: 1px solid #d7dbdd;
    font-size: 14px;
  }

  .form-group textarea {
    resize: vertical;
  }

  .error {
    color: #e74c3c;
    font-size: 12px;
    margin-top: 3px;
    display: block;
  }
</style>

<div class="specialite-form-wrapper">
  <div class="specialite-form-header">
    <h2>${empty specialite.id ? 'Nouvelle spécialité' : 'Modifier spécialité'}</h2>
    <a href="${pageContext.request.contextPath}/specialites" class="btn btn-secondary">← Retour à la liste</a>
  </div>

  <div class="specialite-form-card">
    <form:form method="post"
               action="${pageContext.request.contextPath}/specialites/save"
               modelAttribute="specialite">

      <form:hidden path="id"/>

      <div class="form-group">
        <label>Code</label>
        <form:input path="code" required="true"/>
        <form:errors path="code" cssClass="error"/>
      </div>

      <div class="form-group">
        <label>Nom / Libellé</label>
        <form:input path="nom" required="true"/>
        <form:errors path="nom" cssClass="error"/>
      </div>

      <div class="form-group">
        <label>Description</label>
        <form:textarea path="description" rows="3"/>
      </div>

      <div>
        <button type="submit" class="btn btn-success">Enregistrer</button>
        <a href="${pageContext.request.contextPath}/specialites" class="btn">Annuler</a>
      </div>
    </form:form>
  </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
  <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .page-header h2 {
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

  .card {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  }

  table.specialite-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
  }

  table.specialite-table th,
  table.specialite-table td {
    padding: 10px 12px;
    border-bottom: 1px solid #ecf0f1;
    text-align: left;
    font-size: 14px;
  }

  table.specialite-table th {
    background: #f4f6f7;
    font-weight: 600;
    color: #2c3e50;
  }

  table.specialite-table tr:hover {
    background: #f8f9f9;
  }

  .actions-cell .btn {
    font-size: 12px;
    padding: 6px 10px;
  }
</style>

<div class="page-header">
  <h2>Gestion des spécialités</h2>
  <div>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">← Retour au dashboard</a>
    <a href="${pageContext.request.contextPath}/specialites/nouveau" class="btn btn-success">+ Nouvelle spécialité</a>
  </div>
</div>

<c:if test="${not empty success}">
  <div class="alert alert-success">${success}</div>
</c:if>

<c:if test="${not empty error}">
  <div class="alert alert-error">${error}</div>
</c:if>

<div class="card">
  <table class="specialite-table">
    <thead>
    <tr>
      <th>ID</th>
      <th>Code</th>
      <th>Nom</th>
      <th>Description</th>
      <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${specialites}" var="s">
      <tr>
        <td>${s.id}</td>
        <td>${s.code}</td>
        <td>${s.nom}</td>
        <td>${s.description}</td>
        <td class="actions-cell">
          <a href="${pageContext.request.contextPath}/specialites/edit/${s.id}" class="btn btn-warning">Modifier</a>
          <a href="${pageContext.request.contextPath}/specialites/delete/${s.id}"
             class="btn btn-danger"
             onclick="return confirm('Supprimer cette spécialité ?')">Supprimer</a>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>

<jsp:include page="../includes/footer.jsp"/>
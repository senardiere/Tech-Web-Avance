<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
    .patients-list-wrapper {
        margin-bottom: 30px;
    }

    .patients-list-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
    }

    .patients-list-header h2 {
        margin: 0;
        color: #2c3e50;
    }

    .patients-table-card {
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    }

    .patients-table {
        width: 100%;
        border-collapse: collapse;
    }

    .patients-table th,
    .patients-table td {
        padding: 10px 12px;
        border-bottom: 1px solid #ecf0f1;
        font-size: 14px;
    }

    .patients-table th {
        background: #f4f6f7;
        color: #2c3e50;
        font-weight: 600;
        text-align: left;
    }

    .patients-table tr:hover {
        background: #f8f9f9;
    }

    .patients-actions .btn {
        font-size: 12px;
        padding: 6px 10px;
    }
</style>

<div class="patients-list-wrapper">
    <div class="patients-list-header">
        <h2>👤 Liste des patients</h2>
        <a href="${pageContext.request.contextPath}/patients/nouveau" class="btn btn-success">+ Nouveau patient</a>
    </div>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="patients-table-card">
        <table class="patients-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Téléphone</th>
                <th>Email</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${patients}" var="p">
                <tr>
                    <td>${p.id}</td>
                    <td>${p.nom}</td>
                    <td>${p.prenom}</td>
                    <td>${p.telephone}</td>
                    <td>${p.email}</td>
                    <td class="patients-actions">
                        <a href="${pageContext.request.contextPath}/patients/${p.id}" class="btn">Voir</a>
                        <a href="${pageContext.request.contextPath}/patients/edit/${p.id}" class="btn btn-warning">Modifier</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
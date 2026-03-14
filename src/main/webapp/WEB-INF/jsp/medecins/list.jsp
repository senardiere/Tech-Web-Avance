<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
    .medecins-list-wrapper {
        margin-bottom: 30px;
    }

    .medecins-list-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
    }

    .medecins-list-header h2 {
        margin: 0;
        color: #2c3e50;
    }

    .medecins-table-card {
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    }

    .medecins-table {
        width: 100%;
        border-collapse: collapse;
    }

    .medecins-table th,
    .medecins-table td {
        padding: 10px 12px;
        border-bottom: 1px solid #ecf0f1;
        font-size: 14px;
    }

    .medecins-table th {
        background: #f4f6f7;
        color: #2c3e50;
        font-weight: 600;
        text-align: left;
    }

    .medecins-table tr:hover {
        background: #f8f9f9;
    }

    .medecins-actions .btn {
        font-size: 12px;
        padding: 6px 10px;
    }
</style>

<div class="medecins-list-wrapper">
    <div class="medecins-list-header">
        <h2>👨‍⚕️ Liste des médecins</h2>
        <a href="${pageContext.request.contextPath}/medecins/nouveau" class="btn btn-success">+ Nouveau médecin</a>
    </div>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="medecins-table-card">
        <table class="medecins-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Spécialité</th>
                <th>Email</th>
                <th>Téléphone</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${medecins}" var="m">
                <tr>
                    <td>${m.id}</td>
                    <td>${m.nom}</td>
                    <td>${m.prenom}</td>
                    <td>${m.specialite.nom}</td>
                    <td>${m.email}</td>
                    <td>${m.telephone}</td>
                    <td class="medecins-actions">
                        <a href="${pageContext.request.contextPath}/medecins/${m.id}" class="btn">Voir</a>
                        <a href="${pageContext.request.contextPath}/medecins/edit/${m.id}" class="btn btn-warning">Modifier</a>
                        <a href="${pageContext.request.contextPath}/medecins/delete/${m.id}"
                           class="btn btn-danger"
                           onclick="return confirm('Désactiver ce médecin ?')">Désactiver</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
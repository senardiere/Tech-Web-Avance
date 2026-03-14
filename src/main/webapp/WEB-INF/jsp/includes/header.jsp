<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ERP Clinique</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
        .header { background: #2c3e50; color: white; padding: 15px 0; margin-bottom: 30px; }
        .header .container { display: flex; justify-content: space-between; align-items: center; }
        .header h1 { font-size: 24px; }
        .header a { color: white; text-decoration: none; margin-left: 20px; }
        .header a:hover { text-decoration: underline; }
        .user-info { background: #34495e; padding: 5px 15px; border-radius: 20px; }
        .menu { background: white; padding: 15px; margin-bottom: 20px; border-radius: 4px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .menu a { margin-right: 15px; color: #2c3e50; text-decoration: none; font-weight: 500; }
        .menu a:hover { color: #3498db; }
        .alert { padding: 15px; margin-bottom: 20px; border-radius: 4px; }
        .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white;
            text-decoration: none; border-radius: 4px; margin: 2px; border: none; cursor: pointer; }
        .btn:hover { background: #2980b9; }
        .btn-danger { background: #e74c3c; }
        .btn-danger:hover { background: #c0392b; }
        .btn-success { background: #27ae60; }
        .btn-success:hover { background: #229954; }
        .btn-warning { background: #f39c12; }
        .btn-warning:hover { background: #e67e22; }
    </style>
</head>
<body>
<div class="header">
    <div class="container">
        <h1>🏥 ERP Clinique Médicale</h1>

        <%-- Vérifier si l'utilisateur est connecté via userId --%>
        <c:if test="${not empty sessionScope.userId}">
            <div class="user-info">
                <span>👤 ${user.prenom} ${user.nom}</span>
                <a href="${pageContext.request.contextPath}/logout">Déconnexion</a>
            </div>
        </c:if>
    </div>
</div>

<%-- Menu de navigation visible seulement si connecté --%>
<c:if test="${not empty sessionScope.userId}">
<div class="container">
    <div class="menu">
            <%-- Lien dashboard principal --%>
        <c:choose>
            <c:when test="${user.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/dashboard">🏠 Dashboard Admin</a>
            </c:when>
            <c:when test="${user.role == 'MEDECIN'}">
                <a href="${pageContext.request.contextPath}/mon-espace/dashboard">🏠 Dashboard Médecin</a>
            </c:when>
        </c:choose>

            <%-- Liens pour ADMIN --%>
        <c:if test="${user.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/medecins">👨‍⚕️ Médecins</a>
            <a href="${pageContext.request.contextPath}/specialites">🔬 Spécialités</a>
        </c:if>

            <%-- Liens pour tous (accessibles à tous les rôles connectés) --%>
        <a href="${pageContext.request.contextPath}/patients">👤 Patients</a>
        <a href="${pageContext.request.contextPath}/rendezvous">📅 Rendez-vous</a>
        <a href="${pageContext.request.contextPath}/consultations">📋 Consultations</a>

            <%-- Liens spécifiques pour MEDECIN (espace personnel) --%>
        <c:if test="${user.role == 'MEDECIN'}">
            <a href="${pageContext.request.contextPath}/mon-espace/dashboard">👨‍⚕️ Mon espace</a>
            <a href="${pageContext.request.contextPath}/mon-espace/mes-patients">👥 Mes patients</a>
            <a href="${pageContext.request.contextPath}/mon-espace/mes-rendezvous">📅 Mes RDV</a>
            <a href="${pageContext.request.contextPath}/mon-espace/mon-agenda">📆 Mon agenda</a>
            <a href="${pageContext.request.contextPath}/mon-espace/consultations">📋 Mes consultations</a>
        </c:if>
    </div>
</div>
</c:if>

<%-- Petit bouton flottant global vers le dashboard (admin ou médecin) --%>
<c:if test="${not empty sessionScope.userId}">
    <c:choose>
        <c:when test="${user.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/dashboard" class="floating-home-btn" title="Dashboard">🏠</a>
        </c:when>
        <c:when test="${user.role == 'MEDECIN'}">
            <a href="${pageContext.request.contextPath}/mon-espace/dashboard" class="floating-home-btn" title="Mon dashboard">🏠</a>
        </c:when>
    </c:choose>
</c:if>

<%-- Messages d'alerte (succès/erreur) --%>
<c:if test="${not empty success}">
<div class="container">
    <div class="alert alert-success">${success}</div>
</div>
</c:if>

<c:if test="${not empty error}">
<div class="container">
    <div class="alert alert-error">${error}</div>
</div>
</c:if>

<%-- Le contenu principal commencera ici --%>
<div class="container">
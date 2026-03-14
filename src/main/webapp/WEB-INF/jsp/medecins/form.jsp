<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
    .medecin-form-wrapper {
        max-width: 800px;
        margin: 0 auto 30px auto;
    }

    .medecin-form-card {
        background: white;
        padding: 25px;
        border-radius: 12px;
        box-shadow: 0 2px 15px rgba(0,0,0,0.08);
    }

    .medecin-form-title {
        color: #2c3e50;
        margin-bottom: 20px;
        font-size: 22px;
        border-bottom: 2px solid #3498db;
        padding-bottom: 10px;
    }

    .medecin-section {
        background: #f8f9fa;
        padding: 15px 18px;
        border-radius: 8px;
        margin-bottom: 18px;
        border-left: 4px solid #3498db;
    }

    .medecin-section h4 {
        margin: 0 0 12px 0;
        color: #2c3e50;
        font-size: 15px;
    }

    .medecin-form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;
    }

    .medecin-form-group {
        margin-bottom: 12px;
    }

    .medecin-form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: 600;
        font-size: 13px;
        color: #2c3e50;
    }

    .medecin-form-group input,
    .medecin-form-group select {
        width: 100%;
        padding: 8px 10px;
        border-radius: 6px;
        border: 1px solid #d7dbdd;
        font-size: 14px;
    }

    .medecin-form-group small {
        font-size: 11px;
        color: #7f8c8d;
    }

    .field-error {
        color: #e74c3c;
        font-size: 12px;
        margin-top: 3px;
        display: block;
    }
</style>

<div class="medecin-form-wrapper">
    <div class="medecin-form-card">
        <h2 class="medecin-form-title">
            <c:choose>
                <c:when test="${empty medecin.id}">Nouveau médecin</c:when>
                <c:otherwise>Modifier médecin</c:otherwise>
            </c:choose>
        </h2>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/medecins/save"
                   modelAttribute="medecin">

            <form:hidden path="id"/>

            <div class="medecin-section">
                <h4>👤 Identité</h4>
                <div class="medecin-form-row">
                    <div class="medecin-form-group">
                        <label>Nom</label>
                        <form:input path="nom"/>
                        <form:errors path="nom" cssClass="field-error"/>
                    </div>
                    <div class="medecin-form-group">
                        <label>Prénom</label>
                        <form:input path="prenom"/>
                        <form:errors path="prenom" cssClass="field-error"/>
                    </div>
                </div>
            </div>

            <div class="medecin-section">
                <h4>📞 Contact</h4>
                <div class="medecin-form-row">
                    <div class="medecin-form-group">
                        <label>Email</label>
                        <form:input path="email" type="email"/>
                        <form:errors path="email" cssClass="field-error"/>
                    </div>
                    <div class="medecin-form-group">
                        <label>Téléphone</label>
                        <form:input path="telephone"/>
                    </div>
                </div>
            </div>

            <div class="medecin-section">
                <h4>🏥 Informations médicales</h4>
                <div class="medecin-form-row">
                    <div class="medecin-form-group">
                        <label>Numéro de licence</label>
                        <form:input path="numeroLicence"/>
                    </div>
                    <div class="medecin-form-group">
                        <label>Cabinet</label>
                        <form:input path="cabinet"/>
                    </div>
                </div>
                <div class="medecin-form-group">
                    <label>Spécialité</label>
                    <form:select path="specialite.id">
                        <option value="">-- Sélectionner --</option>
                        <c:forEach items="${specialites}" var="s">
                            <form:option value="${s.id}">${s.nom}</form:option>
                        </c:forEach>
                    </form:select>
                </div>
            </div>

            <div class="medecin-section">
                <h4>🔐 Compte de connexion</h4>
                <div class="medecin-form-row">
                    <div class="medecin-form-group">
                        <label>Login</label>
                        <form:input path="login"/>
                        <form:errors path="login" cssClass="field-error"/>
                    </div>
                    <div class="medecin-form-group">
                        <label>Mot de passe</label>
                        <form:password path="motDePasse" required="${empty medecin.id}"/>
                        <c:if test="${not empty medecin.id}">
                            <small>Laissez vide pour conserver le mot de passe actuel</small>
                        </c:if>
                    </div>
                </div>
            </div>

            <div style="display:flex; gap:10px; justify-content:flex-end; margin-top:15px;">
                <a href="${pageContext.request.contextPath}/medecins" class="btn">Annuler</a>
                <button type="submit" class="btn btn-success">Enregistrer</button>
            </div>
        </form:form>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
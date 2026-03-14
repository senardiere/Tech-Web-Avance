<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
    .consult-view-wrapper {
        max-width: 900px;
        margin: 0 auto 30px auto;
    }

    .consult-view-card {
        background: white;
        padding: 25px;
        border-radius: 12px;
        box-shadow: 0 2px 15px rgba(0,0,0,0.08);
        margin-bottom: 20px;
    }

    .consult-view-title {
        color: #2c3e50;
        margin-bottom: 15px;
        font-size: 22px;
        border-bottom: 2px solid #3498db;
        padding-bottom: 8px;
    }

    .consult-view-section-title {
        margin: 0 0 10px 0;
        color: #2c3e50;
        font-size: 16px;
    }

    .consult-view-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;
    }

    .consult-view-table th {
        width: 200px;
        text-align: left;
        color: #7f8c8d;
        padding: 6px 0;
    }

    .consult-view-table td {
        padding: 6px 0;
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
</style>

<div class="consult-view-wrapper">
    <div class="consult-view-card">
        <h2 class="consult-view-title">
            Consultation
            <span style="font-size:14px;font-weight:normal;color:#7f8c8d;">
                –
                <c:choose>
                    <c:when test="${not empty consultation.dateConsultation}">
                        ${consultation.dateConsultation}
                    </c:when>
                    <c:otherwise>
                        Date non renseignée
                    </c:otherwise>
                </c:choose>
            </span>
        </h2>

        <h3 class="consult-view-section-title">Informations générales</h3>
        <table class="consult-view-table">
            <tr>
                <th>Patient :</th>
                <td>${consultation.patient.prenom} ${consultation.patient.nom}</td>
            </tr>
            <tr>
                <th>Médecin :</th>
                <td>Dr. ${consultation.medecin.prenom} ${consultation.medecin.nom}</td>
            </tr>
            <tr>
                <th>Statut :</th>
                <td>
                    <c:choose>
                        <c:when test="${consultation.statut == 'EN_COURS'}">
                            <span class="badge-consult badge-en-cours">En cours</span>
                        </c:when>
                        <c:when test="${consultation.statut == 'TERMINEE'}">
                            <span class="badge-consult badge-terminee">Terminée</span>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
        </table>
    </div>

    <div class="consult-view-card">
        <h3 class="consult-view-section-title">Diagnostic</h3>
        <p style="white-space: pre-line; font-size:14px;">
            ${empty consultation.diagnostic ? 'Aucun diagnostic' : consultation.diagnostic}
        </p>
    </div>

    <div class="consult-view-card">
        <h3 class="consult-view-section-title">Prescriptions</h3>
        <p style="white-space: pre-line; font-size:14px;">
            ${empty consultation.prescriptions ? 'Aucune prescription' : consultation.prescriptions}
        </p>
    </div>

    <div class="consult-view-card">
        <h3 class="consult-view-section-title">Actions</h3>
        <a href="${pageContext.request.contextPath}/consultations" class="btn">Retour</a>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
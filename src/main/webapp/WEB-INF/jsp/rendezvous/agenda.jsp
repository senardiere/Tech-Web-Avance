<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/medecins/${medecin.id}" class="btn" style="padding:4px 10px;font-size:12px;">← Fiche médecin</a>
</div>

<style>
    .agenda-wrapper {
        margin-bottom: 30px;
    }

    .agenda-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin: 10px 0 20px 0;
    }

    .agenda-header-title {
        margin: 0;
        color: #2c3e50;
        font-size: 22px;
    }

    .agenda-date-pill {
        background: #f4f6f7;
        padding: 8px 14px;
        border-radius: 999px;
        font-size: 13px;
        color: #2c3e50;
    }

    .agenda-nav .btn {
        font-size: 12px;
        padding: 6px 10px;
    }

    .agenda-card {
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    }

    .agenda-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;
    }

    .agenda-table th,
    .agenda-table td {
        padding: 8px 10px;
        border-bottom: 1px solid #ecf0f1;
    }

    .agenda-table th {
        background: #f4f6f7;
        color: #2c3e50;
        font-weight: 600;
        text-align: left;
    }

    .agenda-slot-libre {
        background: #f9f9f9;
        color: #7f8c8d;
        font-size: 13px;
    }

    .agenda-slot-plein-prevu {
        background: #fff3cd;
    }

    .agenda-slot-plein-valide {
        background: #d4edda;
    }

    .agenda-slot-plein-annule {
        background: #f8d7da;
    }

    .agenda-status-badge {
        display: inline-block;
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 600;
    }

    .badge-prevu {
        background: #f39c12;
        color: white;
    }

    .badge-valide {
        background: #27ae60;
        color: white;
    }

    .badge-annule {
        background: #e74c3c;
        color: white;
    }
</style>

<div class="agenda-wrapper">
    <div class="agenda-header">
        <h2 class="agenda-header-title">📅 Agenda - Dr. ${medecin.prenom} ${medecin.nom}</h2>
        <div class="agenda-nav">
            <a href="${pageContext.request.contextPath}/rendezvous/medecin/${medecin.id}?date=${datePrecedente}" class="btn">← Jour précédent</a>
            <a href="${pageContext.request.contextPath}/rendezvous/medecin/${medecin.id}" class="btn">Aujourd'hui</a>
            <a href="${pageContext.request.contextPath}/rendezvous/medecin/${medecin.id}?date=${dateSuivante}" class="btn">Jour suivant →</a>
        </div>
    </div>

    <div class="agenda-date-pill">
        Jour sélectionné : ${date}
    </div>

    <div class="agenda-card">
        <table class="agenda-table">
            <thead>
            <tr>
                <th>Heure</th>
                <th>Patient</th>
                <th>Motif</th>
                <th>Statut</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="heure" begin="8" end="18">
                <c:set var="heureFormat" value="${heure}:00"/>
                <c:set var="rdvTrouve" value="false"/>

                <c:forEach items="${rendezvous}" var="rdv">
                    <c:if test="${rdv.dateHeure.hour == heure}">
                        <c:set var="rdvTrouve" value="true"/>
                        <tr class="
                            <c:choose>
                                <c:when test='${rdv.statut == \"PREVU\"}'>agenda-slot-plein-prevu</c:when>
                                <c:when test='${rdv.statut == \"VALIDE\"}'>agenda-slot-plein-valide</c:when>
                                <c:when test='${rdv.statut == \"ANNULE\"}'>agenda-slot-plein-annule</c:when>
                            </c:choose>
                        ">
                            <td>${rdv.dateHeure.toLocalTime()}</td>
                            <td>${rdv.patient.prenom} ${rdv.patient.nom}</td>
                            <td>${rdv.motif}</td>
                            <td>
                                <span class="agenda-status-badge
                                    <c:choose>
                                        <c:when test='${rdv.statut == \"PREVU\"}'> badge-prevu</c:when>
                                        <c:when test='${rdv.statut == \"VALIDE\"}'> badge-valide</c:when>
                                        <c:when test='${rdv.statut == \"ANNULE\"}'> badge-annule</c:when>
                                    </c:choose>
                                ">
                                    ${rdv.statut}
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/rendezvous/${rdv.id}" class="btn" style="font-size:12px;padding:4px 8px;">Voir</a>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>

                <c:if test="${!rdvTrouve}">
                    <tr class="agenda-slot-libre">
                        <td>${heure}:00</td>
                        <td colspan="4" style="text-align: center;">Libre</td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
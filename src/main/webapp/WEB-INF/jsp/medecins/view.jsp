<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<style>
    .medecin-view-wrapper {
        display: grid;
        grid-template-columns: 1.1fr 1.4fr;
        gap: 20px;
        margin-bottom: 30px;
    }

    .medecin-card, .medecin-agenda-card {
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    }

    .medecin-card table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;
    }

    .medecin-card th {
        width: 200px;
        text-align: left;
        color: #7f8c8d;
        padding: 6px 0;
    }

    .medecin-card td {
        padding: 6px 0;
    }

    .agenda-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 13px;
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
    }

    .agenda-empty {
        padding: 12px;
        text-align: center;
        color: #7f8c8d;
        font-size: 13px;
    }

    .mini-agenda-card {
        margin-top: 15px;
    }

    .mini-agenda-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 13px;
    }

    .mini-agenda-table th,
    .mini-agenda-table td {
        padding: 6px 8px;
        border-bottom: 1px solid #ecf0f1;
    }

    .mini-agenda-table th {
        background: #f4f6f7;
        color: #2c3e50;
        font-weight: 600;
        text-align: left;
    }

    .mini-slot-libre {
        background: #f9f9f9;
        color: #7f8c8d;
        font-size: 12px;
    }

    .mini-slot-plein {
        font-size: 12px;
    }
</style>

<h2>Dr. ${medecin.prenom} ${medecin.nom}</h2>

<div class="medecin-view-wrapper">
    <div class="medecin-card">
        <table>
            <tr>
                <th>Nom complet :</th>
                <td>Dr. ${medecin.prenom} ${medecin.nom}</td>
            </tr>
            <tr>
                <th>Spécialité :</th>
                <td>${medecin.specialite.nom}</td>
            </tr>
            <tr>
                <th>Numéro de licence :</th>
                <td>${medecin.numeroLicence}</td>
            </tr>
            <tr>
                <th>Email :</th>
                <td>${medecin.email}</td>
            </tr>
            <tr>
                <th>Téléphone :</th>
                <td>${medecin.telephone}</td>
            </tr>
            <tr>
                <th>Cabinet :</th>
                <td>${medecin.cabinet}</td>
            </tr>
            <tr>
                <th>Login :</th>
                <td>${medecin.login}</td>
            </tr>
            <tr>
                <th>Statut :</th>
                <td>
                    <c:choose>
                        <c:when test="${medecin.actif}">
                            <span style="color: green;">Actif</span>
                        </c:when>
                        <c:otherwise>
                            <span style="color: red;">Inactif</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>

        <div style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/medecins/edit/${medecin.id}" class="btn btn-warning">Modifier</a>
            <a href="${pageContext.request.contextPath}/medecins" class="btn">Retour</a>
        </div>
    </div>

    <div class="medecin-agenda-card">
        <h3 style="margin-top:0; margin-bottom:10px;">📅 Prochains rendez-vous</h3>
        <c:choose>
            <c:when test="${empty prochainsRendezVous}">
                <div class="agenda-empty">
                    Aucun rendez-vous à venir pour ce médecin.
                </div>
            </c:when>
            <c:otherwise>
                <table class="agenda-table">
                    <thead>
                    <tr>
                        <th>Date</th>
                        <th>Heure</th>
                        <th>Patient</th>
                        <th>Motif</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${prochainsRendezVous}" var="rdv">
                        <tr>
                            <td>${rdv.dateHeure.toLocalDate()}</td>
                            <td>${rdv.dateHeure.toLocalTime()}</td>
                            <td>${rdv.patient.prenom} ${rdv.patient.nom}</td>
                            <td>${rdv.motif}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <div style="margin-top: 10px; text-align:right;">
            <a href="${pageContext.request.contextPath}/rendezvous/medecin/${medecin.id}" class="btn" style="font-size:12px;padding:6px 10px;">
                Voir l'agenda détaillé
            </a>
        </div>

        <div class="mini-agenda-card">
            <h3 style="margin-top:15px; margin-bottom:8px; font-size:14px;">
                📆 Agenda du jour (${agendaDate})
            </h3>
            <table class="mini-agenda-table">
                <thead>
                <tr>
                    <th>Heure</th>
                    <th>Patient</th>
                    <th>Motif</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="heure" begin="8" end="18">
                    <c:set var="rdvTrouve" value="false"/>

                    <c:forEach items="${agendaRendezVous}" var="rdv">
                        <c:if test="${rdv.dateHeure.hour == heure}">
                            <c:set var="rdvTrouve" value="true"/>
                            <tr class="mini-slot-plein">
                                <td>${rdv.dateHeure.toLocalTime()}</td>
                                <td>${rdv.patient.prenom} ${rdv.patient.nom}</td>
                                <td>${rdv.motif}</td>
                            </tr>
                        </c:if>
                    </c:forEach>

                    <c:if test="${!rdvTrouve}">
                        <tr class="mini-slot-libre">
                            <td>${heure}:00</td>
                            <td colspan="2" style="text-align:center;">Libre</td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
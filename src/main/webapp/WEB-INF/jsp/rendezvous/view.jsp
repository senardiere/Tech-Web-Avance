<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<h2>Détails du rendez-vous</h2>

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin: 20px 0;">
    <div style="background: white; padding: 20px; border-radius: 5px;">
        <h3>Informations générales</h3>
        <table style="width: 100%;">
            <tr>
                <th style="width: 40%;">Date et heure :</th>
                <td>
                    <c:choose>
                        <c:when test="${not empty rendezVous.dateHeure}">
                            ${rendezVous.dateHeure}
                        </c:when>
                        <c:otherwise>
                            Non renseignée
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th>Durée :</th>
                <td>${rendezVous.duree} minutes</td>
            </tr>
            <tr>
                <th>Statut :</th>
                <td>
                    <c:choose>
                        <c:when test="${rendezVous.statut == 'PREVU'}">
                            <span style="color: orange; font-weight: bold;">Prévu</span>
                        </c:when>
                        <c:when test="${rendezVous.statut == 'VALIDE'}">
                            <span style="color: green; font-weight: bold;">Validé</span>
                        </c:when>
                        <c:when test="${rendezVous.statut == 'ANNULE'}">
                            <span style="color: red; font-weight: bold;">Annulé</span>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th>Motif :</th>
                <td>${rendezVous.motif}</td>
            </tr>
            <c:if test="${not empty rendezVous.motifAnnulation}">
                <tr>
                    <th>Motif annulation :</th>
                    <td style="color: red;">${rendezVous.motifAnnulation}</td>
                </tr>
            </c:if>
            <tr>
                <th>Date de prise :</th>
                <td>
                    <c:choose>
                        <c:when test="${not empty rendezVous.datePrise}">
                            ${rendezVous.datePrise}
                        </c:when>
                        <c:otherwise>
                            Non renseignée
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>
    </div>

    <div style="background: white; padding: 20px; border-radius: 5px;">
        <h3>Patient</h3>
        <table style="width: 100%;">
            <tr>
                <th style="width: 40%;">Nom complet :</th>
                <td>${rendezVous.patient.prenom} ${rendezVous.patient.nom}</td>
            </tr>
            <tr>
                <th>Téléphone :</th>
                <td>${rendezVous.patient.telephone}</td>
            </tr>
        </table>

        <h3 style="margin-top: 20px;">Médecin</h3>
        <table style="width: 100%;">
            <tr>
                <th style="width: 40%;">Nom complet :</th>
                <td>Dr. ${rendezVous.medecin.prenom} ${rendezVous.medecin.nom}</td>
            </tr>
            <tr>
                <th>Spécialité :</th>
                <td>${rendezVous.medecin.specialite.libelle}</td>
            </tr>
        </table>
    </div>
</div>

<div style="background: white; padding: 20px; border-radius: 5px;">
    <h3>Actions</h3>

    <c:if test="${rendezVous.statut == 'PREVU'}">
        <a href="${pageContext.request.contextPath}/rendezvous/valider/${rendezVous.id}"
           class="btn btn-success">Valider</a>
        <a href="${pageContext.request.contextPath}/rendezvous/annuler/${rendezVous.id}"
           class="btn btn-danger">Annuler</a>
    </c:if>

    <c:if test="${rendezVous.statut == 'VALIDE' and empty rendezVous.consultation}">
        <a href="${pageContext.request.contextPath}/consultations/nouvelle?rdvId=${rendezVous.id}"
           class="btn">Démarrer consultation</a>
    </c:if>

    <a href="${pageContext.request.contextPath}/rendezvous" class="btn">Retour</a>
</div>

<jsp:include page="../includes/footer.jsp"/>
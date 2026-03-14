<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../includes/header.jsp"/>

<div style="margin-bottom: 10px;">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="padding:4px 10px;font-size:12px;">← Dashboard</a>
</div>

<h2>Fiche patient : ${patient.prenom} ${patient.nom}</h2>

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin: 20px 0;">
    <div style="background: white; padding: 20px; border-radius: 5px;">
        <h3>Informations personnelles</h3>
        <table style="width: 100%;">
            <tr>
                <th style="width: 40%;">N° Sécurité sociale :</th>
                <td>${patient.numeroSecuriteSociale}</td>
            </tr>
            <tr>
                <th>Date naissance :</th>
                <td>${patient.dateNaissance}</td>
            </tr>
            <tr>
                <th>Téléphone :</th>
                <td>${patient.telephone}</td>
            </tr>
            <tr>
                <th>Email :</th>
                <td>${patient.email}</td>
            </tr>
            <tr>
                <th>Adresse :</th>
                <td>${patient.adresse}</td>
            </tr>
            <tr>
                <th>Mutuelle :</th>
                <td>${patient.mutuelle}</td>
            </tr>
            <tr>
                <th>Contact urgence :</th>
                <td>${patient.personneContact} (${patient.telephoneContact})</td>
            </tr>
            <tr>
                <th>Statut :</th>
                <td>
                    <c:choose>
                        <c:when test="${patient.statut == 'ACTIF'}">
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
            <a href="${pageContext.request.contextPath}/patients/edit/${patient.id}" class="btn btn-warning">Modifier</a>
            <a href="${pageContext.request.contextPath}/rendezvous/nouveau?patientId=${patient.id}" class="btn btn-success">Nouveau RDV</a>
            <a href="${pageContext.request.contextPath}/patients" class="btn">Retour</a>
        </div>
    </div>

    <div style="background: white; padding: 20px; border-radius: 5px;">
        <h3>Historique des rendez-vous</h3>
        <table>
            <thead>
            <tr>
                <th>Date</th>
                <th>Médecin</th>
                <th>Statut</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${patient.rendezVous}" var="rdv">
                <tr>
                    <td><fmt:formatDate value="${rdv.dateHeure}" pattern="dd/MM/yyyy HH:mm"/></td>
                    <td>Dr. ${rdv.medecin.prenom} ${rdv.medecin.nom}</td>
                    <td>
                        <c:choose>
                            <c:when test="${rdv.statut == 'PREVU'}">Prévu</c:when>
                            <c:when test="${rdv.statut == 'VALIDE'}">Validé</c:when>
                            <c:when test="${rdv.statut == 'ANNULE'}">Annulé</c:when>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
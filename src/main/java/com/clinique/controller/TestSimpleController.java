package com.clinique.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.clinique.dao.UtilisateurDAO;
import com.clinique.entity.Utilisateur;
import com.clinique.entity.Admin;
import com.clinique.entity.Medecin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

@RestController
public class TestSimpleController {

    @Autowired
    private UtilisateurDAO utilisateurDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/ping")
    public String ping() {
        return "✅ PONG! L'application est en marche!";
    }

    @GetMapping("/test-db")
    public String testDb() {
        try {
            Query query = entityManager.createNativeQuery("SELECT 1");
            Object result = query.getSingleResult();
            return "✅ Connexion DB OK! MySQL répond: " + result;
        } catch (Exception e) {
            return "❌ Erreur DB: " + e.getMessage();
        }
    }

    @GetMapping("/debug-sql")
    public String debugSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: monospace; padding: 20px;'>");
        sb.append("<h2>🔍 DIAGNOSTIC COMPLET DE LA BASE</h2>");

        try {
            // 1. Compter les utilisateurs
            Number count = (Number) entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM utilisateur").getSingleResult();
            sb.append("<h3>📊 Statistiques</h3>");
            sb.append("Total utilisateurs: <b>").append(count).append("</b><br><br>");

            // 2. Voir tous les utilisateurs avec leurs types
            List<Object[]> users = entityManager.createNativeQuery(
                            "SELECT id, nom, prenom, type_utilisateur, role, " +
                                    "LENGTH(type_utilisateur) as longueur, " +
                                    "QUOTE(type_utilisateur) as valeur_exacte " +
                                    "FROM utilisateur ORDER BY id")
                    .getResultList();

            sb.append("<h3>📋 Détails des utilisateurs</h3>");
            sb.append("<table border='1' cellpadding='8' style='border-collapse: collapse;'>");
            sb.append("<tr style='background: #f0f0f0;'>");
            sb.append("<th>ID</th><th>Nom</th><th>Prénom</th><th>type_utilisateur</th>");
            sb.append("<th>Longueur</th><th>Valeur exacte</th><th>Rôle</th><th>Statut</th>");
            sb.append("</tr>");

            for (Object[] row : users) {
                sb.append("<tr>");
                sb.append("<td>").append(row[0]).append("</td>");
                sb.append("<td>").append(row[1]).append("</td>");
                sb.append("<td>").append(row[2]).append("</td>");
                sb.append("<td>").append(row[3]).append("</td>");
                sb.append("<td>").append(row[5]).append("</td>");
                sb.append("<td>").append(row[6]).append("</td>");
                sb.append("<td>").append(row[4]).append("</td>");

                // Vérifier si le type est correct
                String type = row[3] != null ? row[3].toString().trim() : "";
                String statut = "";
                String couleur = "";

                if (type.isEmpty()) {
                    statut = "❌ NULL ou VIDE";
                    couleur = "red";
                } else if ("Admin".equals(type) && "ADMIN".equals(row[4])) {
                    statut = "✅ OK";
                    couleur = "green";
                } else if ("Medecin".equals(type) && "MEDECIN".equals(row[4])) {
                    statut = "✅ OK";
                    couleur = "green";
                } else {
                    statut = "❌ INVALIDE (doit être 'Admin' ou 'Medecin')";
                    couleur = "red";
                }

                sb.append("<td style='color: ").append(couleur).append("; font-weight: bold;'>");
                sb.append(statut).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

            // 3. Suggestion de correction
            sb.append("<h3>🛠️ Correction automatique</h3>");
            sb.append("<pre style='background: #f5f5f5; padding: 10px;'>");
            sb.append("-- Exécutez ces requêtes dans phpMyAdmin pour corriger :\n\n");
            sb.append("-- Corriger les admins\n");
            sb.append("UPDATE utilisateur SET type_utilisateur = 'Admin' WHERE role = 'ADMIN';\n\n");
            sb.append("-- Corriger les médecins\n");
            sb.append("UPDATE utilisateur SET type_utilisateur = 'Medecin' WHERE role = 'MEDECIN';\n\n");
            sb.append("-- Vérification après correction\n");
            sb.append("SELECT id, nom, type_utilisateur, role FROM utilisateur;\n");
            sb.append("</pre>");

        } catch (Exception e) {
            sb.append("<h3 style='color: red;'>❌ ERREUR</h3>");
            sb.append("<pre>").append(e.getMessage()).append("</pre>");
            e.printStackTrace();
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    @GetMapping("/test-utilisateurs")
    public String testUtilisateurs() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: monospace; padding: 20px;'>");
        sb.append("<h2>🧪 TEST CHARGEMENT UTILISATEURS</h2>");

        try {
            // Test 1: SQL natif
            List<Object[]> sqlUsers = entityManager.createNativeQuery(
                            "SELECT id, nom, prenom, type_utilisateur FROM utilisateur")
                    .getResultList();

            sb.append("<h3>📊 Via SQL natif (").append(sqlUsers.size()).append(" utilisateurs)</h3>");
            sb.append("<ul>");
            for (Object[] row : sqlUsers) {
                sb.append("<li>ID ").append(row[0])
                        .append(": ").append(row[1]).append(" ").append(row[2])
                        .append(" (type: '").append(row[3]).append("')</li>");
            }
            sb.append("</ul>");

            // Test 2: Tentative JPA (celle qui plante)
            sb.append("<h3>🔄 Tentative JPA...</h3>");
            List<Utilisateur> users = utilisateurDAO.findAll();

            sb.append("<h3 style='color: green;'>✅ SUCCÈS! ").append(users.size()).append(" utilisateurs chargés</h3>");
            sb.append("<ul>");
            for (Utilisateur u : users) {
                sb.append("<li>");
                sb.append(u.getClass().getSimpleName()).append(": ");
                sb.append(u.getPrenom()).append(" ").append(u.getNom());
                sb.append(" (rôle: ").append(u.getRole()).append(")");

                // Afficher les détails spécifiques selon le type
                if (u instanceof Admin) {
                    Admin a = (Admin) u;
                    sb.append(" [Admin - ").append(a.getDepartement()).append("]");
                } else if (u instanceof Medecin) {
                    Medecin m = (Medecin) u;
                    sb.append(" [Médecin - ").append(m.getNumeroLicence()).append("]");
                }
                sb.append("</li>");
            }
            sb.append("</ul>");

        } catch (Exception e) {
            sb.append("<h3 style='color: red;'>❌ ERREUR JPA</h3>");
            sb.append("<pre>").append(e.getMessage()).append("</pre>");
            sb.append("<h4>Cause:</h4>");
            sb.append("<pre>").append(e.getCause()).append("</pre>");

            // Cause racine
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            sb.append("<h4>Cause racine:</h4>");
            sb.append("<pre style='color: red;'>").append(root.getMessage()).append("</pre>");

            sb.append("<h3 style='color: orange;'>🔧 SOLUTION</h3>");
            sb.append("<p>Exécutez ces commandes SQL dans phpMyAdmin :</p>");
            sb.append("<pre style='background: #f5f5f5; padding: 10px;'>");
            sb.append("-- 1. Voir l'état actuel\n");
            sb.append("SELECT id, nom, type_utilisateur, role FROM utilisateur;\n\n");
            sb.append("-- 2. Corriger les types\n");
            sb.append("UPDATE utilisateur SET type_utilisateur = 'Admin' WHERE role = 'ADMIN';\n");
            sb.append("UPDATE utilisateur SET type_utilisateur = 'Medecin' WHERE role = 'MEDECIN';\n\n");
            sb.append("-- 3. Vérifier la correction\n");
            sb.append("SELECT id, nom, type_utilisateur, role FROM utilisateur;\n");
            sb.append("</pre>");
        }

        sb.append("<hr>");
        sb.append("<h3>📌 Récapitulatif des comptes</h3>");
        sb.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        sb.append("<tr><th>Login</th><th>Mot de passe</th><th>Rôle</th></tr>");
        sb.append("<tr><td>admin</td><td>System</td><td>ADMIN</td></tr>");
        sb.append("<tr><td>pbernard</td><td>admin123</td><td>ADMIN</td></tr>");
        sb.append("<tr><td>jdupont</td><td>Jean</td><td>MEDECIN</td></tr>");
        sb.append("<tr><td>smartin</td><td>password123</td><td>MEDECIN</td></tr>");
        sb.append("<tr><td>rlefebvre</td><td>robert123</td><td>MEDECIN</td></tr>");
        sb.append("</table>");

        sb.append("</body></html>");
        return sb.toString();
    }

    @GetMapping("/corriger-types")
    public String corrigerTypes() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: monospace; padding: 20px;'>");
        sb.append("<h2>🔧 CORRECTION AUTOMATIQUE DES TYPES</h2>");

        try {
            // 1. Voir avant correction
            List<Object[]> avant = entityManager.createNativeQuery(
                            "SELECT id, nom, type_utilisateur, role FROM utilisateur")
                    .getResultList();

            sb.append("<h3>Avant correction:</h3>");
            sb.append("<table border='1' cellpadding='5'>");
            for (Object[] row : avant) {
                sb.append("<tr><td>").append(row[0]).append("</td>")
                        .append("<td>").append(row[1]).append("</td>")
                        .append("<td>").append(row[2]).append("</td>")
                        .append("<td>").append(row[3]).append("</td></tr>");
            }
            sb.append("</table>");

            // 2. Exécuter la correction
            int admins = entityManager.createNativeQuery(
                            "UPDATE utilisateur SET type_utilisateur = 'Admin' WHERE role = 'ADMIN'")
                    .executeUpdate();

            int medecins = entityManager.createNativeQuery(
                            "UPDATE utilisateur SET type_utilisateur = 'Medecin' WHERE role = 'MEDECIN'")
                    .executeUpdate();

            sb.append("<h3 style='color: green;'>✅ Correction exécutée</h3>");
            sb.append("<p>Admins corrigés: ").append(admins).append("</p>");
            sb.append("<p>Médecins corrigés: ").append(medecins).append("</p>");

            // 3. Voir après correction
            List<Object[]> apres = entityManager.createNativeQuery(
                            "SELECT id, nom, type_utilisateur, role FROM utilisateur")
                    .getResultList();

            sb.append("<h3>Après correction:</h3>");
            sb.append("<table border='1' cellpadding='5'>");
            for (Object[] row : apres) {
                sb.append("<tr><td>").append(row[0]).append("</td>")
                        .append("<td>").append(row[1]).append("</td>")
                        .append("<td>").append(row[2]).append("</td>")
                        .append("<td>").append(row[3]).append("</td></tr>");
            }
            sb.append("</table>");

        } catch (Exception e) {
            sb.append("<h3 style='color: red;'>❌ Erreur: ").append(e.getMessage()).append("</h3>");
        }

        sb.append("</body></html>");
        return sb.toString();
    }
}
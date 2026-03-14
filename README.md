# ERP Clinique

## Configuration nécessaire
1. Java 21
2. WildFly 39+
3. MySQL (XAMPP ou installation séparée)

## Étapes d'installation
1. Cloner le projet
2. Importer dans IntelliJ
3. Configurer WildFly avec MySQL (module et datasource)
4. Créer la base `clinique_db` dans MySQL
5. Démarrer WildFly avec offset=1
6. Déployer l'application

## Structure du projet
- `entity/` : Entités JPA
- `dao/` : Accès aux données
- `service/` : Logique métier
- `controller/` : ManagedBeans JSF
- `webapp/xhtml/` : Pages JSF
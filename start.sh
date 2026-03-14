#!/bin/bash
# Script de démarrage complet pour ERP Clinique

echo "========================================="
echo "🚀 ERP Clinique - Démarrage automatique"
echo "========================================="

cd "/home/fita/Documents/erp-clinique v1/erp-clinique"

# Étape 1: Compiler le projet
echo "📦 Compilation du projet..."
mvn clean package

# Étape 2: Vérifier si WildFly est déjà installé
if [ ! -d "target/server" ]; then
    echo "🔄 Première installation - Provisionnement de WildFly..."
    mvn wildfly:dev &
    WILDFLY_PID=$!
    sleep 30  # Attendre que WildFly démarre
else
    echo "✅ WildFly déjà installé, démarrage..."
    cd target/server/bin
    ./standalone.sh -Djboss.http.port=9090 &
    WILDFLY_PID=$!
    cd "/home/fita/Documents/erp-clinique v1/erp-clinique"
    sleep 10
fi

# Étape 3: Installer MySQL dans WildFly
echo "🔧 Configuration de MySQL..."

# Créer le module MySQL
mkdir -p target/server/modules/com/mysql/main
cp ~/Téléchargements/mysql-connector-j-8.0.33.jar target/server/modules/com/mysql/main/ 2>/dev/null

# Si le connecteur n'est pas dans Téléchargements, le télécharger
if [ $? -ne 0 ]; then
    echo "📥 Téléchargement du connecteur MySQL..."
    wget -P ~/Téléchargements/ https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
    cp ~/Téléchargements/mysql-connector-j-8.0.33.jar target/server/modules/com/mysql/main/
fi

# Créer module.xml
cat > target/server/modules/com/mysql/main/module.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="com.mysql">
    <resources>
        <resource-root path="mysql-connector-j-8.0.33.jar"/>
    </resources>
    <dependencies>
        <module name="java.sql"/>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
EOF

# Attendre que la CLI soit disponible
sleep 5

# Configurer via CLI
cd target/server/bin
./jboss-cli.sh --connect << 'EOF'
# Ajouter le driver MySQL
/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql, driver-module-name=com.mysql, driver-class-name=com.mysql.cj.jdbc.Driver)

# Ajouter la datasource
data-source add --name=clinique_erp --jndi-name=java:jboss/datasources/clinique_erp --driver-name=mysql --connection-url=jdbc:mysql://localhost:3306/clinique_erp?useSSL=false --user-name=root --password=mysql

# Vérifier
/subsystem=datasources:read-resource
EOF

cd "/home/fita/Documents/erp-clinique v1/erp-clinique"

# Étape 4: Copier le WAR
echo "📋 Déploiement de l'application..."
cp target/erp-clinique.war target/server/standalone/deployments/

# Étape 5: Nettoyer les anciens marqueurs
rm -f target/server/standalone/deployments/erp-clinique.war.failed
touch target/server/standalone/deployments/erp-clinique.war

echo "========================================="
echo "✅ Configuration terminée !"
echo "========================================="
echo "📱 Application : http://localhost:9090/erp-clinique/"
echo "📊 Console admin : http://localhost:9990/console"
echo "🛑 Pour arrêter : Ctrl+C dans ce terminal"
echo "========================================="

# Attendre que l'utilisateur arrête
wait $WILDFLY_PID

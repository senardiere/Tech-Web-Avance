# Guide de Configuration - ERP Clinique Frontend

## Prérequis

- Node.js 18+ 
- npm ou pnpm
- Backend ERP Clinique en cours d'exécution

## Étapes d'installation

### 1. Installer les dépendances

```bash
npm install
# ou
pnpm install
```

### 2. Configurer l'API Backend

Le fichier `.env.local` a déjà été créé avec la configuration par défaut.

**Si votre backend est sur un autre port ou domaine**, mettez à jour `.env.local` :

```bash
NEXT_PUBLIC_API_URL=http://votre-backend:5089/api
```

### 3. Démarrer l'application

```bash
npm run dev
# ou
pnpm dev
```

L'application sera disponible sur `http://localhost:3000`

## Configuration pour la production

### Déploiement sur Vercel

1. Connectez votre repository GitHub à Vercel
2. Dans les paramètres du projet, ajoutez la variable d'environnement :
   - **Name**: `NEXT_PUBLIC_API_URL`
   - **Value**: `https://your-backend.com/api`

3. Déployez votre application

### Déploiement sur un serveur personnalisé

1. Générez une version optimisée :
```bash
npm run build
```

2. Démarrez le serveur de production :
```bash
npm run start
```

## Dépannage

### "Erreur de connexion à l'API"

1. Vérifiez que le backend est en cours d'exécution
2. Vérifiez l'URL de l'API dans `.env.local`
3. Assurez-vous que CORS est activé sur le backend

### "Session expirée"

Le backend gérant les sessions par cookies HTTP-only, assurez-vous que :
- Le backend envoie les headers CORS corrects
- Les cookies cross-site sont autorisés si le frontend et le backend sont sur des domaines différents

### "404 Not Found" sur une page

Assurez-vous que vous êtes connecté. Certaines pages redirigent vers la page de connexion si vous n'êtes pas authentifié.

## Identifiants de test

**Admin**
- Login: `admin`
- Mot de passe: `admin123`

Créez d'autres utilisateurs via l'API ou l'interface d'administration.

## Structure des fichiers

```
/app
  /login              # Page de connexion
  /dashboard          # Tableau de bord principal
  /patients           # Gestion des patients
  /doctors            # Gestion des médecins
  /specialties        # Gestion des spécialités
  /appointments       # Gestion des rendez-vous
  /consultations      # Gestion des consultations
  
/lib
  /api-client.ts      # Client API
  /auth-context.tsx   # Contexte d'authentification
  
/components
  /ui                 # Composants réutilisables
```

## Variables d'environnement disponibles

| Variable | Description | Exemple |
|----------|-------------|---------|
| `NEXT_PUBLIC_API_URL` | URL de base de l'API backend | `http://localhost:5089/api` |

Note: Les variables préfixées par `NEXT_PUBLIC_` sont exposées au navigateur. N'y mettez pas d'informations sensibles.

## Commandes disponibles

```bash
# Développement avec rechargement à chaud
npm run dev

# Build pour la production
npm run build

# Démarrer le serveur de production
npm run start

# Linter et formatage
npm run lint
```

## Support

Pour des problèmes ou des questions :
1. Vérifiez le README_ERP.md pour plus d'informations
2. Consultez la documentation de l'API backend
3. Vérifiez les logs de la console (F12) pour les erreurs

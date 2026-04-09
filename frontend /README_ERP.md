# ERP Clinique - Frontend React

Application React moderne pour la gestion d'une clinique. Interface complète pour gérer les patients, médecins, rendez-vous et consultations.

## Fonctionnalités

### Pour les Administrateurs
- Gestion des patients (création, modification, suppression)
- Gestion des médecins 
- Gestion des spécialités
- Gestion des rendez-vous
- Gestion des consultations
- Tableau de bord avec statistiques

### Pour les Médecins
- Consultation de leurs rendez-vous
- Accès à leurs patients
- Gestion des consultations
- Tableau de bord personnalisé

## Configuration

### 1. Copier le fichier d'environnement

```bash
cp .env.local.example .env.local
```

### 2. Configurer l'API Backend

Éditer `.env.local` et définir l'URL du backend :

```
NEXT_PUBLIC_API_URL=http://localhost:5089/api
```

Adaptez l'URL selon votre configuration :
- **Développement local** : `http://localhost:5089/api`
- **Production** : `https://your-domain.com/api`

### 3. Installer les dépendances

```bash
npm install
# ou
pnpm install
```

### 4. Démarrer le serveur de développement

```bash
npm run dev
# ou
pnpm dev
```

L'application sera disponible sur `http://localhost:3000`

## Authentification

### Identifiants de test

- **Login** : `admin`
- **Mot de passe** : `admin123`
- **Rôle** : Admin

Vous pouvez créer d'autres utilisateurs via l'API backend.

## Structure du projet

```
├── app/
│   ├── login/              # Page de connexion
│   ├── dashboard/          # Tableau de bord
│   ├── patients/           # Gestion des patients
│   ├── doctors/            # Gestion des médecins
│   ├── specialties/        # Gestion des spécialités
│   ├── appointments/       # Gestion des rendez-vous
│   ├── consultations/      # Gestion des consultations
│   └── layout.tsx          # Layout principal avec AuthProvider
│
├── lib/
│   ├── api-client.ts       # Client API avec tous les endpoints
│   ├── auth-context.tsx    # Contexte d'authentification
│   └── utils.ts            # Utilitaires
│
└── components/
    └── ui/                 # Composants UI shadcn
```

## API Endpoints utilisés

L'application utilise les endpoints suivants du backend :

### Authentification
- `POST /auth/login` - Connexion
- `POST /auth/logout` - Déconnexion
- `GET /auth/current-user` - Récupérer l'utilisateur courant

### Patients
- `GET /patients` - Lister tous les patients
- `POST /patients` - Créer un patient
- `GET /patients/{id}` - Récupérer les détails d'un patient
- `PUT /patients/{id}` - Modifier un patient
- `DELETE /patients/{id}` - Supprimer un patient

### Médecins
- `GET /medecins` - Lister tous les médecins
- `GET /medecins/{id}` - Récupérer les détails d'un médecin
- `DELETE /medecins/{id}` - Supprimer un médecin

### Spécialités
- `GET /specialites` - Lister les spécialités
- `POST /specialites` - Créer une spécialité
- `PUT /specialites/{id}` - Modifier une spécialité
- `DELETE /specialites/{id}` - Supprimer une spécialité

### Rendez-vous
- `GET /rendezvous` - Lister les rendez-vous
- `POST /rendezvous` - Créer un rendez-vous
- `PATCH /rendezvous/{id}/valider` - Valider un rendez-vous
- `PATCH /rendezvous/{id}/annuler` - Annuler un rendez-vous
- `DELETE /rendezvous/{id}` - Supprimer un rendez-vous

### Consultations
- `GET /consultations` - Lister les consultations
- `POST /consultations` - Créer une consultation
- `PUT /consultations/{id}` - Modifier une consultation
- `DELETE /consultations/{id}` - Supprimer une consultation

## Technologies utilisées

- **Next.js 15** - Framework React avec SSR
- **React 19** - Bibliothèque UI
- **TypeScript** - Typage statique
- **Tailwind CSS** - Framework CSS
- **shadcn/ui** - Composants UI

## Gestion des erreurs

L'application gère les erreurs d'API avec des messages d'erreur clairs affichés à l'utilisateur. Assurez-vous que votre backend est en cours d'exécution sur le port configuré.

## Support

Pour plus d'informations sur l'API backend, consultez le fichier de documentation de l'API complète.

## Notes importantes

1. **Authentification** : Les sessions sont gérées par cookies HTTP-only via le backend
2. **CORS** : Assurez-vous que votre backend autorise les requêtes CORS depuis l'URL du frontend
3. **Dates** : Les dates sont au format ISO 8601 (YYYY-MM-DDTHH:MM:SS)
4. **Rôles** : 
   - `ADMIN` : Accès complet à toutes les fonctionnalités
   - `MEDECIN` : Accès limité à ses rendez-vous et patients

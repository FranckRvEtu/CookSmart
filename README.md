# CookSmart

## Fonctionnalités d'Authentification

### Écran de Connexion
- Interface intuitive avec champs email et mot de passe
- Validation du format d'email en temps réel
- Option "Se souvenir de moi"
- Bouton de connexion avec état de chargement
- Affichage des messages d'erreur
- Visibilité du mot de passe configurable
- Navigation vers l'inscription

### Processus d'Inscription en 3 Étapes

#### Étape 1 : Profil
- Saisie du prénom et nom
- Validation du format d'email
- Progression visuelle des étapes
- Navigation intuitive

#### Étape 2 : Préférences Alimentaires
- Sélection des allergènes avec système de recherche
- Interface avec chips pour une meilleure UX
- Choix du régime alimentaire
- Liste exhaustive d'options alimentaires

#### Étape 3 : Sécurité
- Création de mot de passe sécurisé
- Indicateurs de force du mot de passe
- Validation des critères en temps réel :
  - Minimum 8 caractères
  - Au moins un chiffre
  - Au moins une majuscule
  - Au moins une minuscule
  - Au moins un caractère spécial
- Confirmation du mot de passe
- Récapitulatif complet des informations

### Caractéristiques Techniques
- Implémentation avec Jetpack Compose
- Architecture MVVM
- Navigation fluide entre les écrans
- Gestion d'état avec MutableState
- Validation des données en temps réel
- Animations et transitions fluides
- Interface Material Design 3

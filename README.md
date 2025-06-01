# CookSmart

An intelligent cooking assistant application.

## Features

### Search Functionality
- Search for recipes by name or ingredients
- Advanced filtering options:
  - Time: Filter recipes by preparation time
  - Difficulty: Filter by recipe difficulty level
  - Price: Filter recipes by estimated cost
  - Without Oven: Toggle to show only recipes that don't require an oven
- AI-Powered Allergen Detection:
  - Automatic analysis of recipe ingredients using Firebase AI with Gemini
  - Real-time identification of common allergens (nuts, dairy, eggs, soy, wheat, fish, shellfish)
  - Visual indicators for recipes containing allergens
- Real-time search results with Material3 UI components
- Responsive grid layout for search results
- Navigation from home screen search bar

### Recipe Management
- Browse popular recipes
- View recipe details including ingredients and steps
- AI cooking assistant integration
- Save favorite recipes

### User Profile
- Personalized recipe recommendations
- Save dietary preferences
- Track cooking history

## Technical Details

### AI and Machine Learning
- Firebase AI integration with Gemini 2.0 Flash Lite model
- Natural language processing for ingredient analysis
- Real-time allergen detection in recipe ingredients
- Intelligent ingredient classification and processing

### Cloud Functions
- `searchMarmitonRecipes`: Search recipes with filters
- `searchRecipesByIngredients`: Find recipes based on available ingredients
- `getEasyRecipes`: Get recipes filtered by difficulty
- `getQuickRecipes`: Get recipes under 30 minutes
- `getBudgetRecipes`: Get cost-effective recipes

### UI Components
- Material3 design system
- Jetpack Compose for modern UI development
- Responsive layouts for different screen sizes
- Dark/light theme support

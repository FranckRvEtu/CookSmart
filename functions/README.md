# Firebase Functions for CookSmart

This directory contains Firebase Cloud Functions that provide recipe search capabilities using the Marmiton API.

## Available Functions

### `searchMarmitonRecipes`

Main search function that allows searching recipes with various filters.

**Parameters:**
```typescript
{
  title?: string;           // Search by recipe title
  maxTime?: number;         // Maximum cooking time in minutes
  difficulty?: RECIPE_DIFFICULTY; // Recipe difficulty level
  price?: RECIPE_PRICE;     // Recipe price range
  withoutOven?: boolean;    // Filter recipes that don't require an oven
  limit?: number;           // Number of results (default: 12)
}
```

### `searchRecipesByIngredients`

Search and process recipes based on available ingredients. Returns recipes sorted by matching ingredients and possibility to make the recipe.

**Parameters:**
```typescript
{
  ingredients: string[];    // List of available ingredients
  searchParams?: {         // Optional search parameters
    title?: string;
    maxTime?: number;
    difficulty?: RECIPE_DIFFICULTY;
    price?: RECIPE_PRICE;
    withoutOven?: boolean;
    limit?: number;
  }
}
```

**Returns ProcessedRecipe objects that extend Recipe with:**
```typescript
{
  matchingIngredients: string[];      // List of ingredients you have
  matchingIngredientsCount: number;   // Number of ingredients you have
  canBeMadeWithIngredients: boolean;  // True if you have all needed ingredients
}
```

The results are sorted by:
1. Recipes that can be made with available ingredients first
2. Number of matching ingredients (highest to lowest)

### `getEasyRecipes`

Returns a list of easy-to-make recipes.

- No parameters required
- Returns up to 12 recipes by default
- Filters by RECIPE_DIFFICULTY.EASY

### `getQuickRecipes`

Returns a list of quick recipes that take 30 minutes or less.

- No parameters required
- Returns up to 12 recipes by default
- Filters recipes that take less than 30 minutes

### `getBudgetRecipes`

Returns a list of budget-friendly recipes.

- No parameters required
- Returns up to 12 recipes by default
- Filters by RECIPE_PRICE.CHEAP

## Response Format

All functions return responses in the following format:

```typescript
{
  success: boolean;
  data?: Recipe[] | ProcessedRecipe[];  // Array of recipes if successful
  error?: string;   // Error message if failed
  details?: string; // Detailed error information if available
}
```

## Recipe Object Structure

```typescript
interface Recipe {
  author: string;
  ingredients: string[];
  tags: string[];
  steps: string[];
  people: number;
  budget: number;      // 1: Cheap, 2: Medium, 3: Expensive
  difficulty: number;  // 1: Very Easy, 2: Easy, 3: Medium, 4: Hard
  prepTime: number;    // In minutes
  totalTime: number;   // In minutes
}
```

## Error Handling

All functions include proper error handling and will return:
- HTTP 200 with `success: true` and data for successful requests
- HTTP 500 with `success: false` and error details for failed requests

## CORS Support

All endpoints support CORS and can be accessed from the mobile app.

## Usage Examples

```typescript
// Example: Search for quick vegetarian recipes
const response = await fetch('your-firebase-url/searchMarmitonRecipes', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    title: 'vegetarien',
    maxTime: 30,
    limit: 5
  })
});

// Example: Search recipes based on available ingredients
const response = await fetch('your-firebase-url/searchRecipesByIngredients', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    ingredients: ['tomato', 'pasta', 'olive oil'],
    searchParams: {
      maxTime: 30,  // Optional: additional search parameters
      limit: 5
    }
  })
});

const data = await response.json();
// Handle the response...

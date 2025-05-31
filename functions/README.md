# CookSmart Cloud Functions

## Configuration

Environment variables required:
- `MISTRAL_API_KEY`: Your Mistral AI API key

## API Endpoints

All recipe endpoints return recipes in the following JSON format:

```typescript
interface Recipe {
  id: string;           // Randomly generated ID
  difficulty: number;   // 1-4 scale
  images: string;       // Base64 encoded image
  ingredients: string[];
  name: string;
  people: number;       // Number of servings
  preptime: number;     // Preparation time in minutes
  price: number;        // Estimated price
  steps: string[];      // Cooking instructions
  tags: string[];       // Recipe tags (e.g., "Easy", "Quick", "Vegetarian")
  type: string;         // Recipe type (e.g., "DESSERT", "DINNER")
  withOven: boolean;    // Whether the recipe requires an oven
}
```

### Available Endpoints

1. `searchRecipesByIngredients`
   - Finds recipes that can be made with specified ingredients
   - Body:
     ```typescript
     {
       ingredients: string[];     // List of available ingredients
       searchParams?: {          // Optional search parameters
         title?: string;         // Recipe title filter
         maxTime?: number;       // Maximum preparation time
         difficulty?: number;     // Difficulty level (1-4)
         withoutOven?: boolean;  // Whether to exclude recipes requiring an oven
       }
     }
     ```
   - Returns an array of recipes with additional fields:
     - `matchingIngredients`: Array of ingredients from your list that are used
     - `matchingIngredientsCount`: Number of matching ingredients
     - `canBeMadeWithIngredients`: Whether all required ingredients are available

2. `searchRecipes`
   - Searches for recipes based on criteria
   - Body:
     ```typescript
     {
       title?: string;         // Recipe title filter
       maxTime?: number;       // Maximum preparation time
       difficulty?: number;    // Difficulty level (1-4)
       withoutOven?: boolean; // Whether to exclude recipes requiring an oven
     }
     ```

3. `getEasyRecipes`
   - Returns beginner-friendly recipes (difficulty level 1)
   - No request body needed

4. `getQuickRecipes`
   - Returns recipes that can be prepared in 30 minutes or less
   - No request body needed

5. `getBudgetRecipes`
   - Returns affordable recipes with low-cost ingredients
   - No request body needed

## Response Format

All endpoints return responses in the following format:

```typescript
{
  success: boolean;
  data?: Recipe[];
  error?: string;
  details?: string;
}
```

## Error Handling

The API may return the following status codes:
- 200: Success
- 400: Bad Request (invalid parameters)
- 500: Internal Server Error
- 408: Request Timeout

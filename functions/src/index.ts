import { onRequest, HttpsOptions } from "firebase-functions/v2/https";

// Configuration for all functions
const functionConfig: HttpsOptions = {
  timeoutSeconds: 120,
  memory: '256MiB'
};
import * as logger from "firebase-functions/logger";
import cors from 'cors';
import { searchRecipes, MarmitonQueryBuilder, RECIPE_PRICE, RECIPE_DIFFICULTY, Recipe } from 'marmiton-api';

// Initialize cors middleware
const corsHandler = cors({ origin: true });

// Search recipes by available ingredients
export const searchRecipesByIngredients = onRequest(functionConfig, async (request, response) => {
  await new Promise(resolve => corsHandler(request, response, resolve));

  try {
    logger.info("Starting searchRecipesByIngredients request");
    const { searchParams, ingredients } = request.body;
    
    if (!Array.isArray(ingredients)) {
      throw new Error("ingredients must be an array of strings");
    }

    logger.info("Searching recipes with ingredients:", ingredients);
    
    // First search recipes with standard params
    const qb = new MarmitonQueryBuilder();
    if (searchParams) {
      if (searchParams.title) qb.withTitleContaining(searchParams.title);
      if (searchParams.maxTime) qb.takingLessThan(searchParams.maxTime);
      if (searchParams.difficulty) qb.withDifficulty(searchParams.difficulty);
      if (searchParams.price) qb.withPrice(searchParams.price);
      if (searchParams.withoutOven) qb.withoutOven();
    }

    logger.info("Executing Marmiton query for recipes with ingredients");
    const query = qb.build();
    const recipes: Recipe[] = await searchRecipes(query, {
      limit: searchParams?.limit || 12
    });

    // Process recipes with ingredients
    const processedRecipes = processRecipesByIngredients(recipes, ingredients);

    logger.info(`Found ${processedRecipes.length} processed recipes`);
    response.json({
      success: true,
      data: processedRecipes
    });
  } catch (error) {
    logger.error("Error searching recipes by ingredients:", error);
    response.status(500).json({
      success: false,
      error: "Failed to search recipes by ingredients",
      details: error instanceof Error ? error.message : "Unknown error"
    });
  }
});

// Interfaces for request types
interface SearchParams {
  title?: string;
  maxTime?: number;
  difficulty?: RECIPE_DIFFICULTY;
  price?: RECIPE_PRICE;
  withoutOven?: boolean;
  limit?: number;
}

interface ProcessedRecipe extends Recipe {
  matchingIngredients: string[];
  matchingIngredientsCount: number;
  canBeMadeWithIngredients: boolean;
}

/**
 * Processes recipes based on available ingredients.
 * @param recipes - Array of recipes to process
 * @param availableIngredients - Array of ingredients available to cook with
 * @returns Processed recipes with matching information
 */
function processRecipesByIngredients(recipes: Recipe[], availableIngredients: string[]): ProcessedRecipe[] {
  // Convert available ingredients to lowercase for case-insensitive comparison
  const normalizedAvailableIngredients = availableIngredients.map(ing => ing.toLowerCase());

  // Process each recipe
  const processedRecipes: ProcessedRecipe[] = recipes.map(recipe => {
    // Convert recipe ingredients to lowercase for comparison
    const recipeIngredients = recipe.ingredients.map(ing => ing.toLowerCase());
    
    // Find matching ingredients
    const matchingIngredients = recipe.ingredients.filter(ing => 
      normalizedAvailableIngredients.includes(ing.toLowerCase())
    );

    // Calculate if recipe can be made with available ingredients
    const canBeMadeWithIngredients = recipeIngredients.every(ing =>
      normalizedAvailableIngredients.includes(ing)
    );

    return {
      ...recipe,
      matchingIngredients,
      matchingIngredientsCount: matchingIngredients.length,
      canBeMadeWithIngredients
    };
  });

  // Sort recipes by canBeMadeWithIngredients (true first) and then by matchingIngredientsCount
  return processedRecipes.sort((a, b) => {
    if (a.canBeMadeWithIngredients !== b.canBeMadeWithIngredients) {
      return a.canBeMadeWithIngredients ? -1 : 1;
    }
    return b.matchingIngredientsCount - a.matchingIngredientsCount;
  });
}

// Search recipes from Marmiton with filters
export const searchMarmitonRecipes = onRequest(functionConfig, async (request, response) => {
  await new Promise(resolve => corsHandler(request, response, resolve));

  try {
    const params: SearchParams = request.body;
    logger.info("Starting searchMarmitonRecipes request with params:", params);

    const qb = new MarmitonQueryBuilder();

    // Apply filters based on params
    if (params.title) {
      qb.withTitleContaining(params.title);
    }
    if (params.maxTime) {
      qb.takingLessThan(params.maxTime);
    }
    if (params.difficulty) {
      qb.withDifficulty(params.difficulty);
    }
    if (params.price) {
      qb.withPrice(params.price);
    }
    if (params.withoutOven) {
      qb.withoutOven();
    }

    logger.info("Executing Marmiton query for filtered recipes");
    // Build and execute query
    const query = qb.build();
    const recipes: Recipe[] = await searchRecipes(query, {
      limit: params.limit || 12
    });

    logger.info(`Found ${recipes.length} recipes`);
    response.json({
      success: true,
      data: recipes
    });
  } catch (error) {
    logger.error("Error searching recipes:", error);
    response.status(500).json({
      success: false,
      error: "Failed to search recipes",
      details: error instanceof Error ? error.message : "Unknown error"
    });
  }
});

// Get filtered recipes by difficulty
export const getEasyRecipes = onRequest(functionConfig, async (request, response) => {
  await new Promise(resolve => corsHandler(request, response, resolve));
  
  try {
    logger.info("Starting getEasyRecipes request");
    const qb = new MarmitonQueryBuilder();
    qb.withDifficulty(RECIPE_DIFFICULTY.EASY);
    
    const query = qb.build();
    logger.info("Executing Marmiton query for easy recipes");
    const recipes: Recipe[] = await searchRecipes(query, { limit: 12 });

    logger.info(`Found ${recipes.length} easy recipes`);
    response.json({
      success: true,
      data: recipes
    });
  } catch (error) {
    logger.error("Error fetching easy recipes:", error);
    response.status(500).json({
      success: false,
      error: "Failed to fetch easy recipes",
      details: error instanceof Error ? error.message : "Unknown error"
    });
  }
});

// Get quick recipes (under 30 minutes)
export const getQuickRecipes = onRequest(functionConfig, async (request, response) => {
  await new Promise(resolve => corsHandler(request, response, resolve));
  
  try {
    logger.info("Starting getQuickRecipes request");
    const qb = new MarmitonQueryBuilder();
    qb.takingLessThan(30); // 30 minutes or less
    
    const query = qb.build();
    logger.info("Executing Marmiton query for quick recipes");
    const recipes: Recipe[] = await searchRecipes(query, { limit: 12 });

    logger.info(`Found ${recipes.length} quick recipes`);
    response.json({
      success: true,
      data: recipes
    });
  } catch (error) {
    logger.error("Error fetching quick recipes:", error);
    response.status(500).json({
      success: false,
      error: "Failed to fetch quick recipes",
      details: error instanceof Error ? error.message : "Unknown error"
    });
  }
});

// Get budget-friendly recipes
export const getBudgetRecipes = onRequest(functionConfig, async (request, response) => {
  await new Promise(resolve => corsHandler(request, response, resolve));
  
  try {
    logger.info("Starting getBudgetRecipes request");
    const qb = new MarmitonQueryBuilder();
    qb.withPrice(RECIPE_PRICE.CHEAP);
    
    const query = qb.build();
    logger.info("Executing Marmiton query for budget recipes");
    const recipes: Recipe[] = await searchRecipes(query, { limit: 12 });

    logger.info(`Found ${recipes.length} budget recipes`);
    response.json({
      success: true,
      data: recipes
    });
  } catch (error) {
    logger.error("Error fetching budget recipes:", error);
    response.status(500).json({
      success: false,
      error: "Failed to fetch budget recipes",
      details: error instanceof Error ? error.message : "Unknown error"
    });
  }
});

import { onCall, HttpsOptions } from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";
import MistralClient from "@mistralai/mistralai";
import * as dotenv from 'dotenv';
import axios from 'axios';

// Load environment variables
dotenv.config();

// Configuration for all functions
const functionConfig: HttpsOptions = {
  timeoutSeconds: 120,
  memory: '256MiB'
};

// Initialize Mistral client with API key
const mistralApiKey = process.env.MISTRAL_API_KEY;
logger.info("Environment variable MISTRAL_API_KEY:", !!process.env.MISTRAL_API_KEY);
logger.info("Actual API key value:", process.env.MISTRAL_API_KEY);

const mistral = new MistralClient(mistralApiKey || "");

// Type definitions for function parameters
interface SearchByIngredientsParams {
  searchParams?: {
    title?: string;
    maxTime?: number;
    difficulty?: string;
    withoutOven?: boolean;
  };
  ingredients: string[];
}

interface SearchFiltersParams {
  title?: string;
  maxTime?: number;
  difficulty?: string;
  withoutOven?: boolean;
}

interface MistralRecipe {
  id: string;
  difficulty: number;
  images: string;
  ingredients: string[];
  name: string;
  people: number;
  preptime: number;
  price: number;
  steps: string[];
  tags: string[];
  type: string;
  withOven: boolean;
}

const RECIPE_PROMPT = `You are a cooking expert. Generate recipes based on the given criteria.
Your response must ONLY contain a valid JSON array with exactly 5 recipes.

Each recipe must strictly follow this format:
{
  "id": "<UUID v4, eg: 123e4567-e89b-12d3-a456-426614174000>",
  "difficulty": <integer from 1 to 4>,
  "images": "",
  "ingredients": ["ingredient1", "ingredient2"],
  "name": "<recipe name>",
  "people": <number of servings>,
  "preptime": <minutes>,
  "price": <estimated cost>,
  "steps": ["step1", "step2"],
  "tags": ["tag1", "tag2"],
  "type": "DINNER",
  "withOven": true|false
}

Rules:
1. Response must be parseable JSON
2. Must return exactly 5 recipes
3. No text before or after the JSON array
4. All fields are required
5. Difficulty levels: 1=Easy, 2=Medium, 3=Hard, 4=Expert
6. Recipe types: "DINNER", "DESSERT", "BREAKFAST", "LUNCH", "SNACK"
7. Generate realistic UUIDs for ids
8. Leave images as empty string
9. Price should be a realistic estimate in dollars

Here's the exact response format (replace with actual recipes):
[{"id":"...","difficulty":1,"images":"","ingredients":[],"name":"..."}...]`;

function validateRecipeResponse(data: any): data is MistralRecipe[] {
  try {
    if (typeof data !== 'string') {
      console.log("Data is not a string:", typeof data);
      return false;
    }

    // Remove markdown code block if present
    const cleanData = data.replace(/```json\n|\n```/g, '').trim();
    console.log("Parsing JSON data:", cleanData);
    const recipes = JSON.parse(cleanData);

    if (!Array.isArray(recipes)) {
      console.log("Not an array:", typeof recipes);
      return false;
    }

    if (recipes.length !== 5) {
      console.log("Array length not 5:", recipes.length);
      return false;
    }

    return recipes.every((recipe, index) => {
      const validations = {
        id: typeof recipe.id === 'string',
        difficulty: typeof recipe.difficulty === 'number' && recipe.difficulty >= 1 && recipe.difficulty <= 4,
        ingredients: Array.isArray(recipe.ingredients),
        name: typeof recipe.name === 'string',
        people: typeof recipe.people === 'number',
        preptime: typeof recipe.preptime === 'number',
        price: typeof recipe.price === 'number',
        steps: Array.isArray(recipe.steps),
        tags: Array.isArray(recipe.tags),
        type: typeof recipe.type === 'string',
        withOven: typeof recipe.withOven === 'boolean'
      };

      const failed = Object.entries(validations)
        .filter(([_, valid]) => !valid)
        .map(([field]) => field);

      if (failed.length > 0) {
        console.log(`Recipe ${index} validation failed for fields:`, failed);
        console.log('Recipe:', recipe);
      }

      return failed.length === 0;
    });
  } catch (error) {
    console.log("Validation error:", error);
    return false;
  }
}

async function generateRecipes(prompt: string): Promise<MistralRecipe[]> {
  try {
    const response = await mistral.chat({
      model: "mistral-large-latest",
      messages: [
        { role: "system", content: RECIPE_PROMPT },
        { role: "user", content: prompt }
      ],
      temperature: 0.7
    });

    const content = response.choices[0].message.content;
    console.log("Mistral AI response:", content);

    // Remove markdown code block if present
    const cleanContent = content.replace(/```json\n|\n```/g, '').trim();

    if (!validateRecipeResponse(cleanContent)) {
      throw new Error("Invalid recipe format received from Mistral AI");
    }

    return JSON.parse(cleanContent);
  } catch (error) {
    logger.error("Error generating recipes:", error);
    throw error;
  }
}

// Search recipes by available ingredients
export const searchRecipesByIngredients = onCall<SearchByIngredientsParams>(functionConfig, async (data, context) => {
  try {
    logger.info("Starting searchRecipesByIngredients request");
    const { searchParams, ingredients } = data.data;
    
    if (!Array.isArray(ingredients)) {
      throw new Error("ingredients must be an array of strings");
    }

    logger.info("Searching recipes with ingredients:", ingredients);
    
    let prompt = `Generate recipes that can be made with these ingredients: ${ingredients.join(", ")}.\n`;
    
    if (searchParams) {
      if (searchParams.title) prompt += `The recipe name should contain: ${searchParams.title}\n`;
      if (searchParams.maxTime) prompt += `The preparation time should be less than ${searchParams.maxTime} minutes.\n`;
      if (searchParams.difficulty) prompt += `The difficulty should be ${searchParams.difficulty}.\n`;
      if (searchParams.withoutOven) prompt += `Do not use an oven in the recipe.\n`;
    }

    const recipes = await generateRecipes(prompt);

    // Process recipes with ingredients
    const processedRecipes = recipes.map(recipe => {
      const matchingIngredients = recipe.ingredients.filter(ing => 
        ingredients.map(i => i.toLowerCase()).includes(ing.toLowerCase())
      );

      return {
        ...recipe,
        matchingIngredients,
        matchingIngredientsCount: matchingIngredients.length,
        canBeMadeWithIngredients: recipe.ingredients.every(ing =>
          ingredients.map(i => i.toLowerCase()).includes(ing.toLowerCase())
        )
      };
    });

    logger.info(`Generated ${processedRecipes.length} recipes`);
    return {
      success: true,
      data: processedRecipes
    };
  } catch (error) {
    logger.error("Error searching recipes by ingredients:", error);
    throw new Error(error instanceof Error ? error.message : "Failed to search recipes by ingredients");
  }
});

// Search recipes with filters
export const searchRecipes = onCall<SearchFiltersParams>(functionConfig, async (data, context) => {
  try {
    const params = data.data;
    logger.info("Starting recipe search request with params:", JSON.stringify(params));

    let prompt = "Generate recipes";
    if (params.title) prompt += ` with names containing "${params.title}"`;
    if (params.maxTime) prompt += ` that take less than ${params.maxTime} minutes to prepare`;
    if (params.difficulty) prompt += ` with difficulty level ${params.difficulty}`;
    if (params.withoutOven) prompt += ` that don't require an oven`;

    const recipes = await generateRecipes(prompt);

    logger.info(`Generated ${recipes.length} recipes`);
    return {
      success: true,
      data: recipes
    };
  } catch (error) {
    logger.error("Error searching recipes:", error);
    throw new Error(error instanceof Error ? error.message : "Failed to search recipes");
  }
});

// Get easy recipes
export const getEasyRecipes = onCall<void>(functionConfig, async (data, context) => {
  try {
    logger.info("Starting getEasyRecipes request");
    const prompt = "Generate easy recipes with difficulty level 1 that are perfect for beginners.";
    
    const recipes = await generateRecipes(prompt);

    logger.info(`Generated ${recipes.length} easy recipes`);
    return {
      success: true,
      data: recipes
    };
  } catch (error) {
    logger.error("Error fetching easy recipes:", error);
    throw new Error(error instanceof Error ? error.message : "Failed to fetch easy recipes");
  }
});

// Get quick recipes (under 30 minutes)
export const getQuickRecipes = onCall<void>(functionConfig, async (data, context) => {
  try {
    logger.info("Starting getQuickRecipes request");
    const prompt = "Generate quick recipes that can be prepared in 30 minutes or less.";
    
    const recipes = await generateRecipes(prompt);

    logger.info(`Generated ${recipes.length} quick recipes`);
    return {
      success: true,
      data: recipes
    };
  } catch (error) {
    logger.error("Error fetching quick recipes:", error);
    throw new Error(error instanceof Error ? error.message : "Failed to fetch quick recipes");
  }
});

// Get budget-friendly recipes
export const getBudgetRecipes = onCall<void>(functionConfig, async (data, context) => {
  try {
    logger.info("Starting getBudgetRecipes request");
    const prompt = "Generate budget-friendly recipes using affordable ingredients with low estimated cost.";
    
    const recipes = await generateRecipes(prompt);

    logger.info(`Generated ${recipes.length} budget recipes`);
    return {
      success: true,
      data: recipes
    };
  } catch (error) {
    logger.error("Error fetching budget recipes:", error);
    throw new Error(error instanceof Error ? error.message : "Failed to fetch budget recipes");
  }
});

// Type definition for barcode parameter
interface BarcodeParams {
  barcode: string;
}

// Type definition for OpenFoodFacts API product response
interface OpenFoodFactsProduct {
  status: number;
  product: {
    product_name: string;
    categories_tags: string[];
    quantity: string;
    nutriments: Record<string, any>;
    ingredients_text: string;
    expiration_date?: string;
    image_url?: string;
  };
}

// Get ingredient information by barcode using OpenFoodFacts API v2
export const getIngredientByBarcode = onCall<BarcodeParams>(functionConfig, async (data, context) => {
  try {
    logger.info("Starting getIngredientByBarcode request");
    const { barcode } = data.data;

    if (!barcode) {
      throw new Error("Barcode is required");
    }

    logger.info(`Fetching data for barcode: ${barcode}`);
    
    const response = await axios.get<OpenFoodFactsProduct>(
      `https://world.openfoodfacts.org/api/v2/product/${barcode}`
    );

    if (response.data.status === 0) {
      throw new Error("Product not found");
    }

    const product = response.data.product;

    logger.info(`Successfully retrieved product: ${product.product_name}`);

    return {
      success: true,
      data: {
        name: product.product_name,
        category: product.categories_tags[0]?.replace('en:', '') || 'Other',
        quantity: product.quantity,
        nutrients: product.nutriments,
        ingredients: product.ingredients_text,
        expiryDate: product.expiration_date,
        image: product.image_url,
        confidence: 100, // API provides verified data
        detectedQuantity: product.quantity,
        detectedUnit: product.quantity?.match(/[a-zA-Z]+/)?.[0] || 'unit',
      }
    };
  } catch (error) {
    logger.error("Error fetching ingredient data:", error);
    throw new Error(error instanceof Error ? error.message : "Failed to fetch ingredient data");
  }
});
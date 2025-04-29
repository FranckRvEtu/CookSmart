import { onRequest } from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";
import cors from 'cors';
import { searchRecipes, MarmitonQueryBuilder, RECIPE_PRICE, RECIPE_DIFFICULTY, Recipe } from 'marmiton-api';

// Initialize cors middleware
const corsHandler = cors({ origin: true });

// Interfaces for request types
interface SearchParams {
  title?: string;
  maxTime?: number;
  difficulty?: RECIPE_DIFFICULTY;
  price?: RECIPE_PRICE;
  withoutOven?: boolean;
  limit?: number;
}

// Search recipes from Marmiton with filters
export const searchMarmitonRecipes = onRequest((request, response) => {
  return new Promise(async (resolve) => {
    corsHandler(request, response, async () => {
      try {
        const params: SearchParams = request.body;
        logger.info("Searching recipes with params:", params);

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
        resolve();
      } catch (error) {
        logger.error("Error searching recipes:", error);
        response.status(500).json({
          success: false,
          error: "Failed to search recipes",
          details: error instanceof Error ? error.message : "Unknown error"
        });
        resolve();
      }
    });
  });
});

// Get filtered recipes by difficulty
export const getEasyRecipes = onRequest((request, response) => {
  return new Promise(async (resolve) => {
    corsHandler(request, response, async () => {
      try {
        const qb = new MarmitonQueryBuilder();
        qb.withDifficulty(RECIPE_DIFFICULTY.EASY);
        
        const query = qb.build();
        const recipes: Recipe[] = await searchRecipes(query, { limit: 12 });

        logger.info(`Found ${recipes.length} easy recipes`);
        response.json({
          success: true,
          data: recipes
        });
        resolve();
      } catch (error) {
        logger.error("Error fetching easy recipes:", error);
        response.status(500).json({
          success: false,
          error: "Failed to fetch easy recipes",
          details: error instanceof Error ? error.message : "Unknown error"
        });
        resolve();
      }
    });
  });
});

// Get quick recipes (under 30 minutes)
export const getQuickRecipes = onRequest((request, response) => {
  return new Promise(async (resolve) => {
    corsHandler(request, response, async () => {
      try {
        const qb = new MarmitonQueryBuilder();
        qb.takingLessThan(30); // 30 minutes or less
        
        const query = qb.build();
        const recipes: Recipe[] = await searchRecipes(query, { limit: 12 });

        logger.info(`Found ${recipes.length} quick recipes`);
        response.json({
          success: true,
          data: recipes
        });
        resolve();
      } catch (error) {
        logger.error("Error fetching quick recipes:", error);
        response.status(500).json({
          success: false,
          error: "Failed to fetch quick recipes",
          details: error instanceof Error ? error.message : "Unknown error"
        });
        resolve();
      }
    });
  });
});

// Get budget-friendly recipes

export const getBudgetRecipes = onRequest((request, response) => {
  return new Promise(async (resolve) => {
    corsHandler(request, response, async () => {
      try {
        const qb = new MarmitonQueryBuilder();
        qb.withPrice(RECIPE_PRICE.CHEAP);
        
        const query = qb.build();
        const recipes: Recipe[] = await searchRecipes(query, { limit: 12 });

        logger.info(`Found ${recipes.length} budget recipes`);
        response.json({
          success: true,
          data: recipes
        });
        resolve();
      } catch (error) {
        logger.error("Error fetching budget recipes:", error);
        response.status(500).json({
          success: false,
          error: "Failed to fetch budget recipes",
          details: error instanceof Error ? error.message : "Unknown error"
        });
        resolve();
      }
    });
  });
});
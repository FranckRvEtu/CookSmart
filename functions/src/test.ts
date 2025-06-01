import axios from 'axios';

// Difficulty levels for recipes
enum RECIPE_DIFFICULTY {
  EASY = 1,
  MEDIUM = 2,
  HARD = 3,
  EXPERT = 4
}

const BASE_URL = 'http://127.0.0.1:5001/cooktarpinsmart/us-central1'; // Firebase emulator URL with project and region

// Helper function to delay execution
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// Helper function to make request with timeout and retries
async function makeRequest(endpoint: string, data: any = {}, retries = 2): Promise<axios.AxiosResponse> {
  for (let attempt = 1; attempt <= retries; attempt++) {
    try {
      console.log(`Making request to ${endpoint} (attempt ${attempt}/${retries})`);
      const response = await axios.post(`${BASE_URL}/${endpoint}`, data);
      return response;
    } catch (error: any) {
      if (attempt === retries) throw error;
      console.log(`Attempt ${attempt} failed, retrying after 2 seconds...`);
      await delay(2000); // Wait 2 seconds before retry
    }
  }
  throw new Error('Request failed after all retries'); // This ensures the function always returns or throws
}

async function testEndpoints() {
  try {
    console.log('\n=== Testing searchRecipesByIngredients ===');
    const ingredientSearchResponse = await makeRequest('searchRecipesByIngredients', {
      ingredients: ['tomato', 'pasta', 'garlic', 'olive oil'],
      searchParams: {
        maxTime: 30,
        difficulty: RECIPE_DIFFICULTY.EASY,
        withoutOven: true
      }
    });
    console.log('Search by Ingredients Results:', JSON.stringify(ingredientSearchResponse.data, null, 2));
    await delay(1000);

    console.log('\n=== Testing searchRecipes ===');
    const searchResponse = await makeRequest('searchRecipes', {
      title: 'pasta',
      maxTime: 30,
      difficulty: RECIPE_DIFFICULTY.EASY,
      withoutOven: false
    });
    console.log('Search Results:', JSON.stringify(searchResponse.data, null, 2));
    await delay(1000);

    console.log('\n=== Testing getEasyRecipes ===');
    const easyResponse = await makeRequest('getEasyRecipes');
    console.log('Easy Recipes:', JSON.stringify(easyResponse.data, null, 2));
    await delay(1000);

    console.log('\n=== Testing getQuickRecipes ===');
    const quickResponse = await makeRequest('getQuickRecipes');
    console.log('Quick Recipes:', JSON.stringify(quickResponse.data, null, 2));
    await delay(1000);

    console.log('\n=== Testing getBudgetRecipes ===');
    const budgetResponse = await makeRequest('getBudgetRecipes');
    console.log('Budget Recipes:', JSON.stringify(budgetResponse.data, null, 2));

  } catch (error: any) {
    if (axios.isAxiosError(error)) {
      console.error('Test Error:', {
        endpoint: error.config?.url,
        status: error.response?.status,
        message: error.response?.data || error.message,
        details: error.response?.data?.details || 'No additional details'
      });
    } else {
      console.error('Unexpected error:', error);
    }
    process.exit(1); // Exit with error code
  }
}

// Execute tests
testEndpoints().then(() => console.log('\nTests completed!'));

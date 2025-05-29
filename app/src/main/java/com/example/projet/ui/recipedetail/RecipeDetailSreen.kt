@file:OptIn(ExperimentalMaterial3Api::class)

package  com.example.projet.ui.recipedetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Data classes
data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val prepTime: Int, // in minutes
    val cookTime: Int, // in minutes
    val servings: Int,
    val difficulty: String,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val nutrition: NutritionInfo?,
    val tags: List<String>
)

data class Ingredient(
    val name: String,
    val amount: String,
    val unit: String
)

data class NutritionInfo(
    val calories: Int,
    val protein: String,
    val carbs: String,
    val fat: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onAiAssistantClick: (Int) -> Unit, // Pass recipe ID to AI assistant
    modifier: Modifier = Modifier
) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }

    // TODO: Replace this with your actual data loading implementation
// This could be from a Repository, ViewModel, or API call
    suspend fun loadRecipeById(id: Int): Recipe {
        // Example implementation - replace with your actual data source
        // This could be from Room database, Retrofit API, etc.

        // Simulate network delay
        kotlinx.coroutines.delay(1000)

        // Mock data - replace with your actual implementation
        return Recipe(
            id = id.toString(),
            title = "Sample Recipe $id",
            description = "A delicious sample recipe loaded by ID",
            imageUrl = "https://example.com/recipe-$id.jpg",
            prepTime = 15,
            cookTime = 30,
            servings = 4,
            difficulty = "Medium",
            ingredients = listOf(
                Ingredient("Ingredient 1", "2", "cups"),
                Ingredient("Ingredient 2", "1", "tbsp"),
                Ingredient("Ingredient 3", "500", "g")
            ),
            instructions = listOf(
                "Step 1: Prepare ingredients...",
                "Step 2: Mix everything together...",
                "Step 3: Cook for 30 minutes..."
            ),
            nutrition = NutritionInfo(
                calories = 250,
                protein = "15g",
                carbs = "30g",
                fat = "8g"
            ),
            tags = listOf("Easy", "Healthy", "Quick")
        )
    }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isFavorite by remember { mutableStateOf(false) }

    // Load recipe data when recipeId changes
    LaunchedEffect(recipeId) {
        try {
            isLoading = true
            error = null
            // TODO: Replace with your actual data source (Repository, ViewModel, etc.)
            recipe = loadRecipeById(recipeId)
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        error != null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error loading recipe",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClick) {
                    Text("Go Back")
                }
            }
            return
        }

        recipe == null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Recipe not found")
            }
            return
        }
    }

    val currentRecipe = recipe!!

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("Recipe") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onShareClick) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else LocalContentColor.current
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recipe Image
            item {
                AsyncImage(
                    model = currentRecipe.imageUrl,
                    contentDescription = currentRecipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Recipe Title and Description
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = currentRecipe.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentRecipe.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Recipe Info Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecipeInfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Prep Time",
                        value = "${currentRecipe.prepTime} min"
                    )
                    RecipeInfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Cook Time",
                        value = "${currentRecipe.cookTime} min"
                    )
                    RecipeInfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Servings",
                        value = currentRecipe.servings.toString()
                    )
                    RecipeInfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Difficulty",
                        value = currentRecipe.difficulty
                    )
                }
            }

            // AI Assistant Button
            item {
                Button(
                    onClick = { onAiAssistantClick(recipeId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        Icons.Default.Smartphone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Start AI Cooking Assistant",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Tags
            if (currentRecipe.tags.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentRecipe.tags.take(3).forEach { tag ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(tag) }
                                )
                            }
                        }
                    }
                }
            }

            // Nutrition Info
            currentRecipe.nutrition?.let { nutrition ->
                item {
                    NutritionCard(nutrition = nutrition)
                }
            }

            // Ingredients Section
            item {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(currentRecipe.ingredients) { ingredient ->
                IngredientItem(ingredient = ingredient)
            }

            // Instructions Section
            item {
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            itemsIndexed(currentRecipe.instructions) { index, instruction ->
                InstructionItem(
                    stepNumber = index + 1,
                    instruction = instruction
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun RecipeInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NutritionCard(
    nutrition: NutritionInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nutrition Info",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("Calories", "${nutrition.calories}")
                NutritionItem("Protein", nutrition.protein)
                NutritionItem("Carbs", nutrition.carbs)
                NutritionItem("Fat", nutrition.fat)
            }
        }
    }
}

@Composable
private fun NutritionItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun IngredientItem(
    ingredient: Ingredient,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${ingredient.amount} ${ingredient.unit}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun InstructionItem(
    stepNumber: Int,
    instruction: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = instruction,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
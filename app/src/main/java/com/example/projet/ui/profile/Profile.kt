package com.example.projet.ui.profile
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projet.ui.ingredientlist.IngredientScannerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize() // The Column itself will fill the whole screen
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Mon Profil",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour"
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Paramètres */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Paramètres"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // This LazyColumn will take up the remaining space in the Column
        LazyColumn(
            modifier = Modifier
                .weight(1f) // MODIFICATION HERE
                .fillMaxWidth(), // You might still want it to fill the width
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // Profil utilisateur
                ProfileHeader()
            }

            item {
                // Statistiques
                StatisticsSection()
            }

            item {
                // Mes recettes
                MyRecipesSection(navController)
            }

            item {
                // Mes favoris
                MyFavoritesSection(navController)
            }

            item {
                // Options du profil
                ProfileOptionsSection(navController)
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Marie Dupont",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "Chef amateur passionnée",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Modifier le profil */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Modifier le profil")
            }
        }
    }
}

@Composable
fun StatisticsSection() {
    Text(
        text = "Mes statistiques",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticCard(
            title = "Recettes créées",
            value = "23",
            icon = Icons.Default.MenuBook,
            modifier = Modifier.weight(1f)
        )

        StatisticCard(
            title = "Favoris",
            value = "47",
            icon = Icons.Default.Favorite,
            modifier = Modifier.weight(1f)
        )

        StatisticCard(
            title = "Abonnés",
            value = "156",
            icon = Icons.Default.People,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MyRecipesSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mes recettes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = { /* Voir toutes mes recettes */ }) {
            Text("Voir tout")
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        myRecipes.take(3).forEach { recipe ->
            MyRecipeItem(recipe = recipe, navController = navController)
        }
    }
}

@Composable
fun MyRecipeItem(recipe: Recipe, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("recipeDetail/${recipe.id}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = "Image recette",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Vues",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.views} vues",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { /* Options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options"
                )
            }
        }
    }
}

@Composable
fun MyFavoritesSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mes favoris",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = { /* Voir tous mes favoris */ }) {
            Text("Voir tout")
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        favoriteRecipes.take(3).forEach { recipe ->
            FavoriteRecipeItem(recipe = recipe, navController = navController)
        }
    }
}

@Composable
fun FavoriteRecipeItem(recipe: Recipe, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("recipeDetail/${recipe.id}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = "Image recette",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Par ${recipe.author}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { /* Retirer des favoris */ }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favori",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ProfileOptionsSection(navController: NavController) {
    Text(
        text = "Options",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileOption(
            icon = Icons.Default.Info,
            title = "Ingredients",
            onClick = {
                Log.d("NavigationDebug", "Tentative de navigation vers inglist")
                navController.navigate("inglist") },
        )
        ProfileOption(
            icon = Icons.Default.Settings,
            title = "Paramètres",
            onClick = { /* Ouvrir paramètres */ }
        )

        ProfileOption(
            icon = Icons.Default.Help,
            title = "Aide et support",
            onClick = { /* Ouvrir aide */ }
        )

        ProfileOption(
            icon = Icons.Default.Info,
            title = "À propos",
            onClick = { /* Ouvrir à propos */ }
        )

        ProfileOption(
            icon = Icons.Default.ExitToApp,
            title = "Se déconnecter",
            onClick = { /* Déconnexion */ },
            isDestructive = true
        )
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Aller à",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Data classes
data class Category(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

data class Recipe(
    val id: String,
    val name: String,
    val duration: Int,
    val rating: Int,
    val reviews: Int,
    val author: String = "",
    val views: Int = 0
)

// Sample data
val categories = listOf(
    Category("Entrées", Icons.Default.Restaurant, Color(0xFF4CAF50)),
    Category("Plats", Icons.Default.Dining, Color(0xFF2196F3)),
    Category("Desserts", Icons.Default.Cake, Color(0xFFE91E63)),
    Category("Boissons", Icons.Default.LocalBar, Color(0xFFFF9800)),
    Category("Végétarien", Icons.Default.Eco, Color(0xFF8BC34A))
)

val popularRecipes = listOf(
    Recipe("1", "Spaghetti Carbonara", 25, 5, 124),
    Recipe("2", "Tarte aux pommes", 45, 4, 89),
    Recipe("3", "Salade César", 15, 4, 76),
    Recipe("4", "Risotto aux champignons", 30, 5, 92)
)

val recentRecipes = listOf(
    Recipe("5", "Quiche Lorraine", 40, 4, 67),
    Recipe("6", "Soupe à l'oignon", 35, 4, 45),
    Recipe("7", "Crêpes suzette", 20, 5, 123)
)

val myRecipes = listOf(
    Recipe("8", "Ma lasagne maison", 60, 5, 234, views = 1250),
    Recipe("9", "Cookies au chocolat", 30, 4, 156, views = 890),
    Recipe("10", "Ratatouille provençale", 45, 5, 98, views = 567)
)

val favoriteRecipes = listOf(
    Recipe("11", "Coq au vin", 90, 5, 78, author = "Chef Martin"),
    Recipe("12", "Tiramisu italien", 20, 4, 145, author = "Nonna Rosa"),
    Recipe("13", "Bouillabaisse", 120, 5, 89, author = "Chef Dubois")
)

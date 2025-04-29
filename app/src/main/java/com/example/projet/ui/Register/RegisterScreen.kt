package com.example.projet.ui.Register
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton


import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projet.ui.Login.RegisterData

// Étape 1 d'inscription - Informations personnelles
@Composable
fun RegisterStep1Screen(navController: NavHostController, registerData: MutableState<RegisterData>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(title = "Étape 1/3 : Profil", onBackClick = { navController.popBackStack() })

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = registerData.value.firstName,
            onValueChange = { registerData.value = registerData.value.copy(firstName = it) },
            label = { Text("Prénom") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            interactionSource = remember { MutableInteractionSource() }
        )

        OutlinedTextField(
            value = registerData.value.lastName,
            onValueChange = { registerData.value = registerData.value.copy(lastName = it) },
            label = { Text("Nom") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            interactionSource = remember { MutableInteractionSource() }
        )

        OutlinedTextField(
            value = registerData.value.email,
            onValueChange = { registerData.value = registerData.value.copy(email = it) },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            interactionSource = remember { MutableInteractionSource() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("register_step2") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = registerData.value.firstName.isNotBlank() &&
                    registerData.value.lastName.isNotBlank() &&
                    registerData.value.email.isNotBlank()
        ) {
            Text("Suivant")
        }
    }
}

// Étape 2 d'inscription - Allergènes et Régime alimentaire
@Composable
fun RegisterStep2Screen(navController: NavHostController, registerData: MutableState<RegisterData>) {
    val allergies = listOf(
        "Gluten", "Lactose", "Arachides", "Fruits à coque",
        "Soja", "Œufs", "Poisson", "Crustacés", "Mollusques",
        "Céleri", "Moutarde", "Sésame", "Sulfites", "Lupin"
    )

    val dietTypes = listOf(
        "Omnivore", "Végétarien", "Vegan", "Pescétarien",
        "Flexitarien", "Sans gluten", "Crudivore", "Paléo"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        TopBar(title = "Étape 2/3 : Préférences", onBackClick = { navController.popBackStack() })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Allergènes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) {
            items(allergies) { allergy ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = registerData.value.selectedAllergies.contains(allergy),
                        onCheckedChange = { isChecked ->
                            val updatedList = if (isChecked) {
                                registerData.value.selectedAllergies + allergy
                            } else {
                                registerData.value.selectedAllergies - allergy
                            }
                            registerData.value = registerData.value.copy(selectedAllergies = updatedList)
                        }
                    )
                    Text(
                        text = allergy,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Régime alimentaire",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Column {
            dietTypes.forEach { diet ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = registerData.value.dietType == diet,
                        onClick = {
                            registerData.value = registerData.value.copy(dietType = diet)
                        }
                    )
                    Text(
                        text = diet,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("register_step3") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Suivant")
        }
    }
}

// Étape 3 d'inscription - Mot de passe et finalisation
@Composable
fun RegisterStep3Screen(navController: NavHostController, registerData: MutableState<RegisterData>) {
    var confirmPassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(title = "Étape 3/3 : Sécurité", onBackClick = { navController.popBackStack() })

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = registerData.value.password,
            onValueChange = { newFirstName ->
                registerData.value = registerData.value.copy(password = it)
                passwordsMatch = it == confirmPassword
            },
            label = { Text("Mot de passe") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordsMatch = registerData<ta.value.password == it
            },
            label = { Text("Confirmer le mot de passe") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            isError = !passwordsMatch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        if (!passwordsMatch) {
            Text(
                text = "Les mots de passe ne correspondent pas",
                color = MaterialTheme.colors.error,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                /* TODO: Logique d'enregistrement */
                // Après enregistrement réussi
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = registerData.value.password.length >= 6 && passwordsMatch
        ) {
            Text("S'enregistrer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Récapitulatif des informations
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Récapitulatif",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Nom: ${registerData.value.lastName} ${registerData.value.firstName}")
                Text("Email: ${registerData.value.email}")
                Text("Régime: ${registerData.value.dietType.ifEmpty { "Non spécifié" }}")
                Text(
                    text = "Allergies: ${
                        if (registerData.value.selectedAllergies.isEmpty()) "Aucune"
                        else registerData.value.selectedAllergies.joinToString(", ")
                    }"
                )
            }
        }
    }
}

// Composant de barre supérieure avec titre et flèche retour
@Composable
fun TopBar(title: String, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
        }
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// Configuration de la navigation principale
@Composable
fun AuthNavigation() {
    val navController = rememberNavController()
    val registerData = remember { mutableStateOf(RegisterData()) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register_step1") {
            RegisterStep1Screen(navController, registerData)
        }
        composable("register_step2") {
            RegisterStep2Screen(navController, registerData)
        }
        composable("register_step3") {
            RegisterStep3Screen(navController, registerData)
        }
    }
}

// Point d'entrée pour votre écran principal
@Composable
fun RecipesAuthApp() {
    MaterialTheme {
        AuthNavigation()
    }
}
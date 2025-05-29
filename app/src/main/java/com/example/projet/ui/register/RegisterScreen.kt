package com.example.projet.ui.register

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation


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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projet.ui.login.LoginScreen

@Composable
fun StepProgressIndicator(currentStep: Int, totalSteps: Int = 3) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (step in 1..totalSteps) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 2.dp)
                    .background(
                        color = if (step <= currentStep)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

// Étape 1 d'inscription - Informations personnelles
@Composable
fun RegisterStep1Screen(navController: NavHostController, registerViewModel: RegisterViewModel ) {
    var isEmailValid by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(title = "Étape 1/3 : Profil", onBackClick = { navController.popBackStack() })

        StepProgressIndicator(currentStep = 1)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = registerViewModel.firstName,
            onValueChange = { registerViewModel.onFirstNameChange(it) },
            label = { Text("Prénom") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = registerViewModel.lastName,
            onValueChange = { registerViewModel.onLastNameChange(it) },
            label = { Text("Nom") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            interactionSource = remember { MutableInteractionSource() }
        )

        OutlinedTextField(
            value = registerViewModel.email,
            onValueChange = {
                registerViewModel.onEmailChange(it)
                isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() || it.isEmpty()
            },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            isError = !isEmailValid,
            supportingText = {
                if (!isEmailValid) {
                    Text("Format d'email invalide", color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("register_step2") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = registerViewModel.firstName.isNotBlank() &&
                    registerViewModel.lastName.isNotBlank() &&
                    registerViewModel.email.isNotBlank() &&
                    isEmailValid
        ) {
            Text("Suivant")
        }
    }
}

// Composant Chip personnalisé pour les allergènes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllergyChip(
    text: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = { Text(text) },
        leadingIcon = if (selected) {
            {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null
    )
}

// Étape 2 d'inscription - Allergènes et Régime alimentaire
@Composable
fun RegisterStep2Screen(navController: NavHostController, registerViewModel: RegisterViewModel ) {
    var searchQuery by remember { mutableStateOf("") }
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

        StepProgressIndicator(currentStep = 2)

        Spacer(modifier = Modifier.height(16.dp))

        // Recherche d'allergènes
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Rechercher des allergènes") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Allergènes
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            maxItemsInEachRow = 3
        ) {
            allergies.filter {
                it.lowercase().contains(searchQuery.lowercase())
            }.forEach { allergy ->
                AllergyChip(
                    text = allergy,
                    selected = registerViewModel.selectedAllergies.contains(allergy),
                    onSelectedChange = { isSelected ->
                        val updatedList = if (isSelected) {
                            registerViewModel.selectedAllergies + allergy
                        } else {
                            registerViewModel.selectedAllergies - allergy
                        }
                        registerViewModel.onSelectedAllergiesChange(updatedList)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Régime alimentaire",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                dietTypes.forEach { diet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                registerViewModel.onDietTypeChange(diet)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = registerViewModel.dietType == diet,
                            onClick = {
                                registerViewModel.onDietTypeChange(diet)
                            }
                        )
                        Text(
                            text = diet,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
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
fun RegisterStep3Screen(navController: NavHostController, registerViewModel: RegisterViewModel ) {

    val passwordRequirements = listOf(
        (registerViewModel.password.length >= 8) to "Au moins 8 caractères",
        (registerViewModel.password.any { it.isDigit() }) to "Au moins un chiffre",
        (registerViewModel.password.any { it.isUpperCase() }) to "Au moins une majuscule",
        (registerViewModel.password.any { it.isLowerCase() }) to "Au moins une minuscule",
        (registerViewModel.password.any { !it.isLetterOrDigit() }) to "Au moins un caractère spécial"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(title = "Étape 3/3 : Sécurité", onBackClick = { navController.popBackStack() })

        Spacer(modifier = Modifier.height(32.dp))

        StepProgressIndicator(currentStep = 3)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = registerViewModel.password,
            onValueChange = { newPassword ->
                registerViewModel.onPasswordChange(newPassword)
                registerViewModel.onPasswordMatch(newPassword == registerViewModel.confirmPassword)
            },
            label = { Text("Mot de passe") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { registerViewModel.onVisibilityChange(!registerViewModel.isPasswordVisible) }) {
                    Icon(
                        imageVector = if (registerViewModel.isPasswordVisible) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        contentDescription = if (registerViewModel.isPasswordVisible) "Cacher le mot de passe"
                        else "Montrer le mot de passe"
                    )
                }
            },
            visualTransformation = if (registerViewModel.isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true,
            supportingText = {
                Column {
                    passwordRequirements.forEach { (requirement, description) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (requirement) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (requirement) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp),
                                color = if (requirement) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        )

        OutlinedTextField(
            value = registerViewModel.confirmPassword,
            onValueChange = {
                registerViewModel.onConfirmChange(it)
                registerViewModel.onPasswordMatch(registerViewModel.password == it)
            },
            label = { Text("Confirmer le mot de passe") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { registerViewModel.onVisibilityChange(!registerViewModel.isPasswordVisible)}) {
                    Icon(
                        imageVector = if (registerViewModel.isPasswordVisible) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        contentDescription = if (registerViewModel.isPasswordVisible) "Cacher le mot de passe"
                        else "Montrer le mot de passe"
                    )
                }
            },
            visualTransformation = if (registerViewModel.isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            isError = !registerViewModel.passwordMatch && registerViewModel.confirmPassword.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            supportingText = {
                if (!registerViewModel.passwordMatch && registerViewModel.confirmPassword.isNotEmpty()) {
                    Text(
                        text = "Les mots de passe ne correspondent pas",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true
        )

        if (!registerViewModel.passwordMatch) {
            Text(
                text = "Les mots de passe ne correspondent pas",
                color = MaterialTheme.colorScheme.error,
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
            enabled = registerViewModel.password.length >= 6 && registerViewModel.passwordMatch
        ) {
            Text("S'enregistrer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Récapitulatif des informations
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Récapitulatif",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Nom: ${registerViewModel.lastName} ${registerViewModel.firstName}")
                Text("Email: ${registerViewModel.email}")
                Text("Régime: ${registerViewModel.dietType.ifEmpty { "Non spécifié" }}")
                Text(
                    text = "Allergies: ${
                        if (registerViewModel.selectedAllergies.isEmpty()) "Aucune"
                        else registerViewModel.selectedAllergies.joinToString(", ")
                    }"
                )
            }
        }
    }
}

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

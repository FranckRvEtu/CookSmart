package com.example.projet.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController


@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel.isLoggedIn) {
        if (viewModel.isLoggedIn) {
            navController.navigate("homescreen") {
                popUpTo("login") { inclusive = true }
            }
        }else{
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo/Title
            Text(
                text = "CookSmart",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Bienvenue",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Error Message
            AnimatedVisibility(
                visible = viewModel.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                viewModel.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChange(it) },
                isError = viewModel.email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches(),
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = {viewModel.onPasswordChange(it)},
                label = { Text("Mot de passe") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { viewModel.onViewPasswordChange(!viewModel.viewPassword)}) {
                        Icon(
                            imageVector = if (viewModel.viewPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (viewModel.viewPassword) "Cacher le mot de passe" else "Montrer le mot de passe"
                        )
                    }
                },
                visualTransformation = if (viewModel.viewPassword) androidx.compose.ui.text.input.VisualTransformation.None
                                     else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Remember me checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.rememberMe,
                    onCheckedChange = { viewModel.onRememberChange(it) }
                )
                Text(
                    text = "Se souvenir de moi",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Button(
                onClick = {
                    isLoading = true
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches()) {
                        viewModel.onErrorMessageChange("Format d'email invalide")
                        isLoading = false
                        return@Button
                    }
                    //viewModel.login()
                    viewModel.login2(
                        onSuccess = {
                            navController.navigate("homescreen") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onFail = {
                            isLoading = false
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isLoading && viewModel.email.isNotBlank() && viewModel.password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Se connecter")
                }
            }

            TextButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Pas encore de compte ? S'inscrire")
            }
        }
    }
}
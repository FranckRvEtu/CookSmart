package com.example.projet.ui.ingredientlist

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionState
import java.util.UUID
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientScannerScreen(navController: NavController) {
    var isScanning by remember { mutableStateOf(false) }
    var availableIngredients by remember { mutableStateOf(sampleAvailableIngredients.toMutableList()) }
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Mes Ingrédients",
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
                IconButton(onClick = { /* Ajouter manuellement */ }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter manuellement"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Scanner") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Mes Ingrédients") }
            )
        }

        when (selectedTab) {
            0 -> ScannerTab(
                isScanning = isScanning,
                onScanToggle = { isScanning = !isScanning },
                onIngredientScanned = { ingredient ->
                    val newIngredient = AvailableIngredient(
                        id = (availableIngredients.size + 1).toString(),
                        name = ingredient.name,
                        category = ingredient.category,
                        quantity = ingredient.detectedQuantity ?: "1",
                        unit = ingredient.detectedUnit ?: "pièce",
                        expiryDate = ingredient.estimatedExpiry,
                        isExpiringSoon = false
                    )
                    Log.d("IngredientScannerScreen", "Adding ingredient to available list: $newIngredient")
                    availableIngredients.add(newIngredient)
                }
            )
            1 -> MyIngredientsTab(
                ingredients = availableIngredients,
                onDeleteIngredient = { ingredient ->
                    availableIngredients.remove(ingredient)
                },
                onUpdateQuantity = { ingredient, newQuantity ->
                    val index = availableIngredients.indexOf(ingredient)
                    if (index != -1) {
                        availableIngredients[index] = ingredient.copy(quantity = newQuantity)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerTab(
    isScanning: Boolean,
    onScanToggle: () -> Unit,
    onIngredientScanned: (ScannedIngredient) -> Unit,
) {
    val viewModel: IngredientListViewModel = viewModel()
    val barcodeState by viewModel.barcodeState.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("ScannerTab", "Initial barcodeState: $barcodeState")
    }

    LaunchedEffect(barcodeState) {
        when (barcodeState) {
            is BarcodeState.Error -> {
                Log.e("ScannerTab", "Error state: ${(barcodeState as BarcodeState.Error).message}")
            }
            is BarcodeState.Success -> {
                Log.d("ScannerTab", "Success state with ingredient: ${(barcodeState as BarcodeState.Success).ingredient}")
            }
            else -> Log.d("ScannerTab", "State changed to: $barcodeState")
        }
    }

    // Track scanned ingredient visibility
    var isCardVisible by remember { mutableStateOf(false) }
    var currentIngredient by remember { mutableStateOf<ScannedIngredient?>(null) }

    // Update visibility and current ingredient when barcode state changes
    LaunchedEffect(barcodeState) {
        Log.d("ScannerTab", "BarcodeState changed, updating visibility: $barcodeState")
        when (barcodeState) {
            is BarcodeState.Success -> {
                currentIngredient = (barcodeState as BarcodeState.Success).ingredient
                isCardVisible = true
                Log.d("ScannerTab", "Showing ingredient card for: ${currentIngredient?.name}")
            }
            BarcodeState.Idle -> {
                isCardVisible = false
                Log.d("ScannerTab", "Hiding ingredient card")
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Camera Scanner Section
            item {
                CameraScannerSection(
                    isScanning = isScanning,
                    onScanToggle = onScanToggle,
                    onIngredientScanned = onIngredientScanned
                )
            }

            // Instructions
            item {
                InstructionsCard()
            }

            // Scanned Ingredient Card with animation
            item {
                AnimatedVisibility(
                    visible = isCardVisible && currentIngredient != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    currentIngredient?.let { ingredient ->
                        ScannedIngredientCard(
                            ingredient = ingredient,
                            onAddToAvailable = { quantity, unit ->
                                onIngredientScanned(ingredient.copy(
                                    detectedQuantity = quantity,
                                    detectedUnit = unit
                                ))
                                viewModel.resetState()
                                onScanToggle()
                            },
                            onScanToggle = onScanToggle
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScannerSection(
    isScanning: Boolean,
    onScanToggle: () -> Unit,
    onIngredientScanned: (ScannedIngredient) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: IngredientListViewModel = viewModel()
    val barcodeState by viewModel.barcodeState.collectAsState()
    
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    LaunchedEffect(Unit) {
        Log.d("CameraScannerSection", "Initial state - Permission: ${cameraPermissionState.status.isGranted}, Scanning: $isScanning")
    }
    
    LaunchedEffect(barcodeState) {
        Log.d("CameraScannerSection", "BarcodeState changed to: $barcodeState")
    }
    
    LaunchedEffect(cameraPermissionState.status.isGranted, isScanning) {
        Log.d("CameraScannerSection", "Permission/Scanning state changed - Permission: ${cameraPermissionState.status.isGranted}, Scanning: $isScanning")
        if (cameraPermissionState.status.isGranted && isScanning) {
            viewModel.startScanning()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isScanning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = if (isScanning) MaterialTheme.colorScheme.secondary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                when {
                    !cameraPermissionState.status.isGranted -> {
                        PermissionRequest(cameraPermissionState)
                    }
                    isScanning -> {
                        CameraPreview(
                            context = context,
                            lifecycleOwner = lifecycleOwner
                        ) { barcode ->
                            viewModel.processBarcode(barcode)
                        }
                        
                        // Scanning overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CropFree,
                                    contentDescription = "Scanner zone",
                                    modifier = Modifier.size(100.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (barcodeState is BarcodeState.Loading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Caméra",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pointez votre caméra vers\nles ingrédients",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Display scanning UI only
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (barcodeState is BarcodeState.Error) {
                Text(
                    text = (barcodeState as BarcodeState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    Log.d("ScannerTab", "Scan button clicked, current scanning state: $isScanning")
                    if (isScanning) {
                        Log.d("ScannerTab", "Stopping scanning")
                        viewModel.stopScanning()
                    } else {
                        Log.d("ScannerTab", "Starting scanning")
                    }
                    onScanToggle()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isScanning) Icons.Default.Stop else Icons.Default.QrCodeScanner,
                    contentDescription = if (isScanning) "Arrêter" else "Scanner",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isScanning) "Arrêter le scan" else "Commencer le scan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRequest(
    cameraPermissionState: PermissionState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Permission d'accès à la caméra requise",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { cameraPermissionState.launchPermissionRequest() }
        ) {
            Text("Autoriser")
        }
    }
}

@Composable
private fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onBarcodeDetected: (String) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { previewView ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                BarcodeAnalyzer(onBarcodeDetected)
            )
            
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

@Composable
fun InstructionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Comment scanner ?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "• Assurez-vous d'avoir un bon éclairage\n" +
                        "• Tenez l'ingrédient face à la caméra\n" +
                        "• Attendez la détection automatique\n" +
                        "• Vérifiez les informations avant d'ajouter",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ScannedIngredientCard(
    ingredient: ScannedIngredient,
    onAddToAvailable: (String, String) -> Unit,
    onScanToggle: () -> Unit
) {
    val viewModel: IngredientListViewModel = viewModel()
    val barcodeState by viewModel.barcodeState.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("ScannedIngredientCard", "Card initialized with ingredient: $ingredient")
        Log.d("ScannedIngredientCard", "Current barcode state: $barcodeState")
    }
    var quantity by remember { mutableStateOf(ingredient.detectedQuantity ?: "1") }
    var unit by remember { mutableStateOf(ingredient.detectedUnit ?: "pièce") }
    
    DisposableEffect(ingredient) {
        Log.d("ScannedIngredientCard", "Initial quantity: $quantity, unit: $unit")
        onDispose {}
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = ingredient.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = ingredient.category,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Confidence Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when {
                        ingredient.confidence >= 90 -> Color(0xFF4CAF50)
                        ingredient.confidence >= 70 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                ) {
                    Text(
                        text = "${ingredient.confidence}%",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity and Unit inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantité") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unité") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (ingredient.estimatedExpiry != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Expiration",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Expire ${ingredient.estimatedExpiry}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { 
                        Log.d("ScannedIngredientCard", "Ignore button clicked - resetting state")
                        viewModel.resetState()
                        onScanToggle()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Ignorer",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ignorer")
                }

                Button(
                    onClick = { 
                        Log.d("ScannedIngredientCard", "Add button clicked with quantity: $quantity, unit: $unit")
                        onAddToAvailable(quantity, unit)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter")
                }
            }
        }
    }
}

@Composable
fun MyIngredientsTab(
    ingredients: List<AvailableIngredient>,
    onDeleteIngredient: (AvailableIngredient) -> Unit,
    onUpdateQuantity: (AvailableIngredient, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Summary Card
            IngredientsSummaryCard(ingredients = ingredients)
        }

        if (ingredients.isEmpty()) {
            item {
                EmptyIngredientsCard()
            }
        } else {
            items(ingredients.groupBy { it.category }.toList()) { (category, categoryIngredients) ->
                IngredientCategorySection(
                    category = category,
                    ingredients = categoryIngredients,
                    onDeleteIngredient = onDeleteIngredient,
                    onUpdateQuantity = onUpdateQuantity
                )
            }
        }
    }
}

@Composable
fun IngredientsSummaryCard(ingredients: List<AvailableIngredient>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryItem(
                title = "Total",
                value = ingredients.size.toString(),
                icon = Icons.Default.Inventory
            )

            SummaryItem(
                title = "Expire bientôt",
                value = ingredients.count { it.isExpiringSoon }.toString(),
                icon = Icons.Default.Warning
            )

            SummaryItem(
                title = "Catégories",
                value = ingredients.distinctBy { it.category }.size.toString(),
                icon = Icons.Default.Category
            )
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun EmptyIngredientsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = "Vide",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Aucun ingrédient",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Scannez vos ingrédients pour commencer",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun IngredientCategorySection(
    category: String,
    ingredients: List<AvailableIngredient>,
    onDeleteIngredient: (AvailableIngredient) -> Unit,
    onUpdateQuantity: (AvailableIngredient, String) -> Unit
) {
    Column {
        Text(
            text = "$category (${ingredients.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ingredients.forEach { ingredient ->
            AvailableIngredientCard(
                ingredient = ingredient,
                onDelete = { onDeleteIngredient(ingredient) },
                onUpdateQuantity = { newQuantity -> onUpdateQuantity(ingredient, newQuantity) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AvailableIngredientCard(
    ingredient: AvailableIngredient,
    onDelete: () -> Unit,
    onUpdateQuantity: (String) -> Unit
) {
    var showQuantityDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ingredient.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${ingredient.quantity} ${ingredient.unit}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    if (ingredient.expiryDate != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Expiration",
                                modifier = Modifier.size(14.dp),
                                tint = if (ingredient.isExpiringSoon) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = ingredient.expiryDate,
                                fontSize = 12.sp,
                                color = if (ingredient.isExpiringSoon) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            IconButton(onClick = { showQuantityDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier quantité"
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showQuantityDialog) {
        var newQuantity by remember { mutableStateOf(ingredient.quantity) }

        AlertDialog(
            onDismissRequest = { showQuantityDialog = false },
            title = { Text("Modifier la quantité") },
            text = {
                OutlinedTextField(
                    value = newQuantity,
                    onValueChange = { newQuantity = it },
                    label = { Text("Quantité") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdateQuantity(newQuantity)
                        showQuantityDialog = false
                    }
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuantityDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

// Data classes
data class ScannedIngredient(
    val id: String,
    val name: String,
    val category: String,
    val confidence: Int,
    val detectedQuantity: String? = null,
    val detectedUnit: String? = null,
    val estimatedExpiry: String? = null
)

data class AvailableIngredient(
    val id: String,
    val name: String,
    val category: String,
    val quantity: String,
    val unit: String,
    val expiryDate: String? = null,
    val isExpiringSoon: Boolean = false
)

// Sample data
val sampleScannedIngredients = listOf(
    ScannedIngredient("1", "Tomate", "Légumes", 95),
    ScannedIngredient("2", "Lait", "Produits laitiers", 88),
    ScannedIngredient("3", "Pomme", "Fruits", 92),
    ScannedIngredient("4", "Baguette", "Boulangerie", 85)
)

val sampleAvailableIngredients = listOf(
    AvailableIngredient("1", "Tomates cerises", "Légumes", "250", "g", "Dans 3 jours", false),
    AvailableIngredient("2", "Lait entier", "Produits laitiers", "1", "L", "Dans 2 jours", true),
    AvailableIngredient("3", "Pommes Golden", "Fruits", "6", "pièces", "Dans 7 jours", false),
    AvailableIngredient("4", "Pâtes", "Épicerie", "500", "g", null, false),
    AvailableIngredient("5", "Fromage râpé", "Produits laitiers", "200", "g", "Demain", true),
    AvailableIngredient("6", "Oignons", "Légumes", "3", "pièces", "Dans 10 jours", false)
)

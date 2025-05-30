
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.projet"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projet"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    kotlinOptions {
        jvmTarget = "19"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions{
        kotlinCompilerExtensionVersion = "2.1.21"
    }
}

dependencies {

    // Kotlin Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Jetpack Compose
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation(libs.firebase.ai)


    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.compose.material:material-icons-extended")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-functions")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.firebaseui:firebase-ui-auth:9.0.0")
    implementation("com.google.firebase:firebase-ai")

    // Si besoin : décommente Firestore et Storage
    // implementation("com.google.firebase:firebase-firestore")
    // implementation("com.google.firebase:firebase-storage")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
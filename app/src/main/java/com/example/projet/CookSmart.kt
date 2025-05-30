package com.example.projet

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.*
import com.google.firebase.Firebase
import com.google.firebase.ai.type.GenerativeBackend

class CookSmart:Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val aiModel = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel("gemini-2.0-flash-lite")
    }
}
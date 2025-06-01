package com.example.projet.ui.ingredientlist

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private var lastScannedBarcode: String? = null
    private var lastScanTime: Long = 0
    private val SCAN_COOLDOWN_MS = 2000 // 2 seconds cooldown between scans

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E
        )
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            Log.d("BarcodeAnalyzer", "Processing new image frame")
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    Log.d("BarcodeAnalyzer", "Image processed, found ${barcodes.size} barcodes")
                    if (barcodes.isEmpty()) {
                        Log.d("BarcodeAnalyzer", "No barcodes found in image")
                    } else {
                        barcodes[0].rawValue?.let { barcode ->
                            val currentTime = System.currentTimeMillis()
                            if (barcode != lastScannedBarcode || 
                                (currentTime - lastScanTime) > SCAN_COOLDOWN_MS) {
                                lastScannedBarcode = barcode
                                lastScanTime = currentTime
                                Log.d("BarcodeAnalyzer", "New barcode detected: $barcode")
                                onBarcodeDetected(barcode)
                            } else {
                                Log.d("BarcodeAnalyzer", "Skipping duplicate barcode or cooldown: $barcode")
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("BarcodeAnalyzer", "Scanner process failed: ${exception.message}")
                }
                .addOnCompleteListener {
                    Log.d("BarcodeAnalyzer", "Scanner process completed")
                    imageProxy.close()
                }
        } else {
            Log.w("BarcodeAnalyzer", "Skipping frame - mediaImage is null")
            imageProxy.close()
        }
    }
}

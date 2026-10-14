package com.ardev.gb.pokemon_yellow

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("sameboy_core")
        }
    }

    private external fun nativeInit(romBytes: ByteArray, isCgb: Boolean)
    private external fun nativeStepFrame(pixelOut: IntArray)
    private external fun nativeSetJoypad(buttons: Int)

    private val screenWidth = 160
    private val screenHeight = 144
    private lateinit var screenBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()

        val romBytes = loadRomAsset("game.gb")
        nativeInit(romBytes, true)

        screenBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        setContentView(R.layout.activity_main)
    }

    private fun loadRomAsset(filename: String): ByteArray {
        val inputStream: InputStream = assets.open(filename)
        return inputStream.readBytes()
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}

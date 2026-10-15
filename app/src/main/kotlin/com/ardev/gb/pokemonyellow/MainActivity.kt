package com.ardev.gb.pokemonyellow

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("sameboy_core")
        }

        // Standard Game Boy Joypad Bitmask
        const val BTN_RIGHT  = 1
        const val BTN_LEFT   = 2
        const val BTN_UP     = 4
        const val BTN_DOWN   = 8
        const val BTN_A      = 16
        const val BTN_B      = 32
        const val BTN_SELECT = 64
        const val BTN_START  = 128
    }

    private external fun nativeInit(romBytes: ByteArray, isCgb: Boolean)
    private external fun nativeStepFrame(pixelOut: IntArray)
    private external fun nativeSetJoypad(buttons: Int)

    private val screenWidth = 160
    private val screenHeight = 144
    private lateinit var screenBitmap: Bitmap
    private val pixelBuffer = IntArray(160 * 144)
    private var joypadState = 0

    private val mainHandler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private lateinit var gameScreen: ImageView

    private val frameRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return
            nativeStepFrame(pixelBuffer)
            screenBitmap.setPixels(pixelBuffer, 0, screenWidth, 0, 0, screenWidth, screenHeight)
            gameScreen.setImageBitmap(screenBitmap)
            mainHandler.postDelayed(this, 16) // ~60 FPS
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()

        val romBytes = loadRomAsset("game.gb")
        nativeInit(romBytes, true)

        screenBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        setContentView(R.layout.activity_main)

        gameScreen = findViewById(R.id.gameScreen)
        setupControls()
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
        mainHandler.post(frameRunnable)
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
        mainHandler.removeCallbacks(frameRunnable)
    }

    private fun loadRomAsset(filename: String): ByteArray {
        val inputStream: InputStream = assets.open(filename)
        return inputStream.readBytes()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupControls() {
        val bindButton = { id: Int, bit: Int ->
            findViewById<View>(id)?.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        joypadState = joypadState or bit
                        nativeSetJoypad(joypadState)
                        hapticFeedback()
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        joypadState = joypadState and bit.inv()
                        nativeSetJoypad(joypadState)
                        true
                    }
                    else -> false
                }
            }
        }

        bindButton(R.id.btnUp, BTN_UP)
        bindButton(R.id.btnDown, BTN_DOWN)
        bindButton(R.id.btnLeft, BTN_LEFT)
        bindButton(R.id.btnRight, BTN_RIGHT)
        bindButton(R.id.btnA, BTN_A)
        bindButton(R.id.btnB, BTN_B)
        bindButton(R.id.btnSelect, BTN_SELECT)
        bindButton(R.id.btnStart, BTN_START)
    }

    private fun hapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(15)
                }
            }
        } catch (_: Exception) {}
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

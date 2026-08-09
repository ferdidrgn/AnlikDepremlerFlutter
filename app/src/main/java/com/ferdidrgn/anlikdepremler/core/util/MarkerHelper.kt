package com.ferdidrgn.anlikdepremler.core.util

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object MarkerHelper {

    fun createCustomMarkerBitmap(context: Context, magnitude: Double): BitmapDescriptor {
        val size = 120
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Şiddete göre renk belirleme
        val colorInt = when {
            magnitude >= 5.0 -> Color.parseColor("#D32F2F") // Koyu Kırmızı
            magnitude >= 3.5 -> Color.parseColor("#E65100") // Turuncu
            else -> Color.parseColor("#2E7D32")             // Yeşil
        }

        // Arka Plan Radyanlı Yuvarlatılmış Kare (Card Effect)
        val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorInt
            style = Paint.Style.FILL
        }
        val rectF = RectF(10f, 10f, size - 10f, size - 10f)
        canvas.drawRoundRect(rectF, 28f, 28f, rectPaint)

        // Beyaz Çerçeve (Border)
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawRoundRect(rectF, 28f, 28f, strokePaint)

        // Şiddet Metni (Magnitude Text)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 38f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val textY = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(String.format("%.1f", magnitude), size / 2f, textY, textPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
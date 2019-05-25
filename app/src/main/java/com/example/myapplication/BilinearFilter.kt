package com.example.myapplication

import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import kotlin.math.sign
import kotlin.math.sqrt
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import android.widget.Toast

class BilinearFilter {
    val coordinates = Array(6) { IntArray(2) }
    var COUNTER = 0
    val inverse = InverseMatrix()
    @SuppressLint("ClickableViewAccessibility")
    fun pickPoints(mainImage: ImageView, context: Context){
        mainImage.setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == 0){
                if (COUNTER > 5){
                    mainImage.setImageBitmap(bilinearFilter(mainImage, coordinates))
                }
                else{
                    Toast.makeText(context, COUNTER.toString(), Toast.LENGTH_SHORT).show()
                    // click coordinates
                    val rawX = event.x
                    val rawY = event.y

                    val oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap
                    val height = oldBitmap.height
                    val width = oldBitmap.width
                    // numberGet bitmap coordinates
                    val x = (rawX.toDouble() * (width.toDouble() / mainImage.width.toDouble())).toInt()
                    val y = (rawY.toDouble() * (height.toDouble() / mainImage.height.toDouble())).toInt()
                    // GET COORDINATES
                    coordinates[COUNTER][0] = x
                    coordinates[COUNTER][1] = y
                    COUNTER++
                }
                // mainImage.invalidate()
            }

            return@OnTouchListener true
        }
        )
    }

    private fun matrixMultiplication(fM: Array<DoubleArray>, sM: Array<DoubleArray>): Array<DoubleArray> {
        // multiplication of two matrix
        val rows1 = fM.size
        val cols1 = fM[0].size
        val rows2 = sM.size
        val cols2 = sM[0].size
        require(cols1 == rows2)
        val result = Array(rows1) { DoubleArray(cols2) }
        for (i in 0 until rows1)
            for (j in 0 until cols2)
                for (k in 0 until rows2)
                    result[i][j] += fM[i][k] * sM[k][j]
        return result
    }

    private fun bilinearFilter(mainImage : ImageView, rawCoordinates: Array<IntArray>) :Bitmap
    {
        val pointsCoordinates = Array(3) { DoubleArray(3) }
        val matr = Array(3) { DoubleArray(3) }
        val newMatr: Array<DoubleArray>

        pointsCoordinates[0][0] = rawCoordinates[0][1].toDouble() // x
        pointsCoordinates[0][1] = rawCoordinates[1][1].toDouble()
        pointsCoordinates[0][2] = rawCoordinates[2][1].toDouble()
        pointsCoordinates[1][0] = rawCoordinates[0][0].toDouble() // y
        pointsCoordinates[1][1] = rawCoordinates[1][0].toDouble()
        pointsCoordinates[1][2] = rawCoordinates[2][0].toDouble()
        pointsCoordinates[2][0] = 1.0
        pointsCoordinates[2][1] = 1.0
        pointsCoordinates[2][2] = 1.0
        // COORDINATES TO FIRST MATRIX
        matr[0][0] = rawCoordinates[3][1].toDouble() // x
        matr[0][1] = rawCoordinates[4][1].toDouble()
        matr[0][2] = rawCoordinates[5][1].toDouble()
        matr[1][0] = rawCoordinates[3][0].toDouble() // y
        matr[1][1] = rawCoordinates[4][0].toDouble()
        matr[1][2] = rawCoordinates[5][0].toDouble()
        matr[2][0] = 1.0
        matr[2][1] = 1.0
        matr[2][2] = 1.0
        // COORDINATES TO SECOND MATRIX

        // T * A = X
        // T = x * A^(-1)
        // INVERSE MATRIX
        val aMatrix = inverse.inverseMatrix(pointsCoordinates)
        newMatr = matrixMultiplication(matr, aMatrix)
        // ANGLE
        val angle:Double = Math.atan2(newMatr[1][0], newMatr[1][1])

        val sx:Double = Math.abs(sign(newMatr[0][0]) * sqrt(newMatr[0][0] * newMatr[0][0] + newMatr[0][1] * newMatr[0][1]))
        val sy:Double = Math.abs(sign(newMatr[1][1]) * sqrt(newMatr[1][0] * newMatr[1][0] + newMatr[1][1] * newMatr[1][1]))

        var resultBitmap = (mainImage.drawable as BitmapDrawable).bitmap
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        val scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, resultBitmap.height, resultBitmap.width, true)
        val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        resultBitmap = rotatedBitmap
        // LAST BITMAP
        return bilinearInterpolate(resultBitmap,sx,sy)
    }

    private fun numberGet(tex: IntArray, x: Int, y: Int, width: Int): Int {
        return if (tex.size > x * width + y && x*width+y >= 0)
            tex[x * width + y]
        else
            0
    }

    private fun definePixelsColors(tex: IntArray, u: Double, v: Double, width: Int, height: Int): Int {
        var uS = u
        var vS = v
        uS = uS * height - 0.5
        vS = vS * width - 0.5
        val x: Int = Math.floor(uS).toInt()
        var y: Int = Math.floor(vS).toInt()
        if (y < 0)
            y = 0
        val newRat = uS - x
        val anotherRat = vS - y
        val newRatOpposite = 1 - newRat
        val anotherRatOpposite = 1 - anotherRat
        // SEPARATE COLOR CHANNELS
        val alpha = Math.floor(((numberGet(tex, x, y, width) shr 24 and 0xff) * newRatOpposite + (numberGet(tex, x + 1, y, width) shr 24 and 0xff) * newRat) * anotherRatOpposite + ((numberGet(tex, x, y + 1, width) shr 24 and 0xff) * newRatOpposite + (numberGet(tex, x + 1, y + 1, width) shr 24 and 0xff) * newRat) * anotherRat).toInt()
        val red = Math.floor(((numberGet(tex, x, y, width) shr 16 and 0xff) * newRatOpposite + (numberGet(tex, x + 1, y, width) shr 16 and 0xff) * newRat) * anotherRatOpposite + ((numberGet(tex, x, y + 1, width) shr 16 and 0xff) * newRatOpposite + (numberGet(tex, x + 1, y + 1, width) shr 16 and 0xff) * newRat) * anotherRat).toInt()
        val green = Math.floor(((numberGet(tex, x, y, width) shr 8 and 0xff) * newRatOpposite + (numberGet(tex, x + 1, y, width) shr 8 and 0xff) * newRat) * anotherRatOpposite + ((numberGet(tex, x, y + 1, width) shr 8 and 0xff) * newRatOpposite + (numberGet(tex, x + 1, y + 1, width) shr 8 and 0xff) * newRat) * anotherRat).toInt()
        val blue = Math.floor(((numberGet(tex, x, y, width) and 0xff) * newRatOpposite + (numberGet(tex, x + 1, y,width) and 0xff) * newRat) * anotherRatOpposite + ((numberGet(tex, x, y + 1, width) and 0xff) * newRatOpposite + (numberGet(tex, x + 1, y + 1, width) and 0xff) * newRat) * anotherRat).toInt()
        return alpha and 0xff shl 24 or (red and 0xff shl 16) or (green and 0xff shl 8) or (blue and 0xff)
    }
    private fun bilinearInterpolate(bitmap: Bitmap, scaleX:Double, scaleY:Double) :Bitmap {
        val height = bitmap.height
        val width = bitmap.width
        // USE SCALE COEFFICIENT TO GET NEW HEIGHT AND NEW WIDTH
        val newHeight = (height * scaleY).toInt()
        val newWidth = (width *scaleX).toInt()
        val oldBitmapPixelsArray = IntArray(width * height)
        // SCALED AND ROTATED BITMAP
        val THE_LAST_BITMAP = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val newBitmapPixelsArray = IntArray(newWidth * newHeight)
        bitmap.getPixels( oldBitmapPixelsArray,0, width,0, 0,  width, height)
        for (row in 0 until newHeight) {
            for (column in 0 until newWidth) {
                val u: Double = (row.toDouble() - 0.5) / (newHeight)
                val v: Double = (column.toDouble() -0.5) / (newWidth)
                newBitmapPixelsArray[row * newWidth + column] = definePixelsColors(oldBitmapPixelsArray, u, v, width, height)
            }
        }
        THE_LAST_BITMAP.setPixels(newBitmapPixelsArray, 0, newWidth, 0, 0, newWidth, newHeight)
        return THE_LAST_BITMAP
    }
}
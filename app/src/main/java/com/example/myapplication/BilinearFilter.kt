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
                    var rawX = event.x
                    var rawY = event.y

                    var oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap
                    var height = oldBitmap.height
                    var width = oldBitmap.width
                    // get bitmap coordinates
                    val x = (rawX.toDouble() * (width.toDouble() / mainImage.width.toDouble())).toInt()
                    val y = (rawY.toDouble() * (height.toDouble() / mainImage.height.toDouble())).toInt()
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

    private fun bilinearFilter(mainImage : ImageView, rawCoordinates: Array<IntArray>) :Bitmap   				//ГЛАВНЫЙ ФУНКЦИЯ
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

        matr[0][0] = rawCoordinates[3][1].toDouble() // x
        matr[0][1] = rawCoordinates[4][1].toDouble()
        matr[0][2] = rawCoordinates[5][1].toDouble()

        matr[1][0] = rawCoordinates[3][0].toDouble() // y
        matr[1][1] = rawCoordinates[4][0].toDouble()
        matr[1][2] = rawCoordinates[5][0].toDouble()

        matr[2][0] = 1.0
        matr[2][1] = 1.0
        matr[2][2] = 1.0
        val Ainv = findInverse(pointsCoordinates)
        newMatr = matr*Ainv


        val angle:Double = Math.atan2(newMatr[1][0], newMatr[1][1])
        val sx:Double = Math.abs(sign(newMatr[0][0]) * sqrt(newMatr[0][0] * newMatr[0][0] + newMatr[0][1] * newMatr[0][1]))
        val sy:Double = Math.abs(sign(newMatr[1][1]) * sqrt(newMatr[1][0] * newMatr[1][0] + newMatr[1][1] * newMatr[1][1]))

        // ЗДЕСЬ ШПИЛИ ВИЛИ ПОВОРОТ
        var bitmap = (mainImage.drawable as BitmapDrawable).bitmap
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.height, bitmap.width, true)
        val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        bitmap = rotatedBitmap
        return bilinearInterpolate(bitmap,sx,sy)

    }

    fun get(tex: IntArray, x: Int, y: Int, width: Int): Int {

        return if (tex.size > x * width + y && x*width+y >= 0)
            tex[x * width + y]
        else
            0
    }

    private fun getPixel(tex: IntArray, u: Double, v: Double, width: Int, height: Int, newWidth: Int, newHeight: Int): Int {
        var uS = u
        var vS = v
        uS = uS * height - 0.5
        vS = vS * width - 0.5
        val x: Int = Math.floor(uS).toInt()
        var y: Int = Math.floor(vS).toInt()
        if (y < 0) {
            y = 0
        }
        val uRatio = uS - x
        val vRatio = vS - y
        val uOpposite = 1 - uRatio
        val vOpposite = 1 - vRatio
        val A = Math.floor(
            ((get(tex, x, y, width) shr 24 and 0xff) * uOpposite + (get(
                tex,
                x + 1,
                y,
                width
            ) shr 24 and 0xff) * uRatio) * vOpposite + ((get(
                tex,
                x,
                y + 1,
                width
            ) shr 24 and 0xff) * uOpposite + (get(tex, x + 1, y + 1, width) shr 24 and 0xff) * uRatio) * vRatio
        )
            .toInt()
        val R = Math.floor(
            ((get(tex, x, y, width) shr 16 and 0xff) * uOpposite + (get(
                tex,
                x + 1,
                y,
                width
            ) shr 16 and 0xff) * uRatio) * vOpposite + ((get(
                tex,
                x,
                y + 1,
                width
            ) shr 16 and 0xff) * uOpposite + (get(tex, x + 1, y + 1, width) shr 16 and 0xff) * uRatio) * vRatio
        )
            .toInt()
        val G = Math.floor(
            ((get(tex, x, y, width) shr 8 and 0xff) * uOpposite + (get(
                tex,
                x + 1,
                y,
                width
            ) shr 8 and 0xff) * uRatio) * vOpposite + ((get(tex, x, y + 1, width) shr 8 and 0xff) * uOpposite + (get(
                tex,
                x + 1,
                y + 1,
                width
            ) shr 8 and 0xff) * uRatio) * vRatio
        )
            .toInt()
        val B = Math.floor(
            ((get(tex, x, y, width) and 0xff) * uOpposite + (get(
                tex,
                x + 1,
                y,
                width
            ) and 0xff) * uRatio) * vOpposite + ((get(tex, x, y + 1, width) and 0xff) * uOpposite + (get(
                tex,
                x + 1,
                y + 1,
                width
            ) and 0xff) * uRatio) * vRatio
        )
            .toInt()
        return A and 0xff shl 24 or (R and 0xff shl 16) or (G and 0xff shl 8) or (B and 0xff)
    }

    private fun bilinearInterpolate(bmp: Bitmap, scale_x:Double, scale_y:Double) :Bitmap {

        val height = bmp.height // высота картинки и битмапа
        val width = bmp.width // ширина
        scale_x
        val new_height = (height * scale_y).toInt()
        val new_width = (width *scale_x).toInt()
        var oldBitmapPixelsArray =
            IntArray(width * height) // массив его пикселей (пока просто массив, не двумерный, и пока он пустой, то есть ничего не содержит, т.е. пока это просто массив длиной width * height)
        var newBitmap = Bitmap.createBitmap(new_width, new_height, Bitmap.Config.ARGB_8888)
        var newBitmapPixelsArray = IntArray(new_width * new_height) // создаем массив пикселей нового битмапа
        bmp.getPixels( oldBitmapPixelsArray,0, width,0, 0,  width, height) // заполняем старый массив пикселей пикселями из старого битмапа
        for (row in 0 until new_height) {
            for (column in 0 until new_width) {
                val color = oldBitmapPixelsArray
                val u: Double = (row.toDouble() - 0.5) / (new_height)
                val v: Double = (column.toDouble() -0.5) / (new_width)
                newBitmapPixelsArray[row * new_width + column] = getPixel(oldBitmapPixelsArray, u, v, width, height, new_width, new_height)
            }
        }

        newBitmap.setPixels(
            newBitmapPixelsArray,
            0,
            new_width,
            0,
            0,
            new_width,
            new_height
        ) // добавляем полученные пиксели в новый битмап
        return newBitmap
    }

    private fun findInverse(mat: Array<DoubleArray>): Array<DoubleArray> {
        var det: Double =
            mat[0][0] * ((mat[1][1] * mat[2][2]) - (mat[2][1] * mat[1][2])) - mat[0][1] * (mat[1][0] * mat[2][2] - mat[2][0] * mat[1][2]) + mat[0][2] * (mat[1][0] * mat[2][1] - mat[2][0] * mat[1][1])
        if (det == 0.0) {
            return Array(3, { DoubleArray(3) })
        }
        var matT = Array(3, { DoubleArray(3) })
        var Adj = Array(3, { DoubleArray(3) })
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                matT[i][j]=mat[j][i]
            }
        }

        Adj[0][0] = matT[1][1]*matT[2][2]-matT[1][2]*matT[2][1]
        Adj[0][1] = -matT[1][0]*matT[2][2]+matT[1][2]*matT[2][0]
        Adj[0][2] = matT[1][0]*matT[2][1]-matT[1][1]*matT[2][0]
        Adj[1][0] = -matT[0][1]*matT[2][2] + matT[0][2]*matT[2][1]
        Adj[1][1] = matT[0][0]*matT[2][2] - matT[0][2]*matT[2][0]
        Adj[1][2] = -matT[0][0]*matT[2][1]+matT[0][1]*matT[2][0]
        Adj[2][0] = matT[0][1]*matT[1][2] - matT[1][1]*matT[0][2]
        Adj[2][1] = -matT[0][0] * matT[1][2] + matT[0][2]*matT[1][0]
        Adj[2][2] = matT[0][0]*matT[1][1] - matT[0][1]*matT[1][0]

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                Adj[i][j]/=det
            }

        }
        return Adj
    }
    operator fun Array<DoubleArray>.times(other: Array<DoubleArray>): Array<DoubleArray> {
        val rows1 = this.size
        val cols1 = this[0].size
        val rows2 = other.size
        val cols2 = other[0].size
        require(cols1 == rows2)
        val result = Array(rows1) { DoubleArray(cols2) }
        for (i in 0 until rows1) {
            for (j in 0 until cols2) {
                for (k in 0 until rows2) {
                    result[i][j] += this[i][k] * other[k][j]
                }
            }
        }
        return result
    }
}
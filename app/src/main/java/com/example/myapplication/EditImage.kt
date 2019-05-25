package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Color.red
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import kotlin.math.roundToInt
import android.view.View
import android.widget.Toast
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class EditImage(BTMP: Bitmap) {
    private var backBitmap = BTMP
    fun returnImage(mainImage: ImageView, savedBitmap: Bitmap){
        mainImage.setImageBitmap(savedBitmap)
    }
    fun enlarge(mainImage: ImageView, selected: String, initialHeight: Int) {
        val scaleCoefficient = selected.removeRange(selected.length - 1, selected.length).toDouble() / 100
        val params = mainImage.layoutParams
        params.height = (initialHeight * scaleCoefficient).roundToInt()
        mainImage.layoutParams = params
    }
    fun returnBackBitmap(): Bitmap {
        return backBitmap
    }

    fun scale05x(context: Context, mainImage: ImageView) {
        val oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap
        val height = oldBitmap.height
        val width = oldBitmap.width
        val oldBitmapPixelsArray = IntArray(width * height)
        val newBitmap = Bitmap.createBitmap(width / 2, height / 2, Bitmap.Config.ARGB_8888)
        val newBitmapPixelsArray = IntArray((width / 2) * (height / 2))
        oldBitmap.getPixels(oldBitmapPixelsArray, 0, width, 0, 0, width, height)

        if (width % 2 == 0) {
            for (i in newBitmapPixelsArray.indices) {
                var redSum = (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width] and 0x00ff0000 shr 16) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + 1] and 0x00ff0000 shr 16) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + width] and 0x00ff0000 shr 16) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + width + 1] and 0x00ff0000 shr 16)
                var greenSum = (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width] and 0x0000ff00 shr 8) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + 1] and 0x0000ff00 shr 8) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + width] and 0x0000ff00 shr 8) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + width + 1] and 0x0000ff00 shr 8)
                var blueSum = (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width] and 0x000000ff shr 0) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + 1] and 0x000000ff shr 0) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + width] and 0x000000ff shr 0) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * width + width + 1] and 0x000000ff shr 0)

                redSum /= 4
                greenSum /= 4
                blueSum /= 4

                newBitmapPixelsArray[i] = ((0xff000000) or (redSum.toLong() shl 16) or
                        (greenSum.toLong() shl 8) or (blueSum.toLong() shl 0)).toInt()
            }
        }
        else {
            for (i in newBitmapPixelsArray.indices) {
                var redSum = (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1)] and 0x00ff0000 shr 16) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + 1] and 0x00ff0000 shr 16) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + (width + 1)] and 0x00ff0000 shr 16) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + (width + 1) + 1] and 0x00ff0000 shr 16)
                var greenSum = (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1)] and 0x0000ff00 shr 8) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + 1] and 0x0000ff00 shr 8) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + (width + 1)] and 0x0000ff00 shr 8) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + (width + 1) + 1] and 0x0000ff00 shr 8)
                var blueSum = (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1)] and 0x000000ff shr 0) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + 1] and 0x000000ff shr 0) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + (width + 1)] and 0x000000ff shr 0) +
                        (oldBitmapPixelsArray[i * 2 + (i / (width / 2)) * (width + 1) + (width + 1) + 1] and 0x000000ff shr 0)

                redSum /= 4
                greenSum /= 4
                blueSum /= 4

                newBitmapPixelsArray[i] = ((0xff000000) or (redSum.toLong() shl 16) or
                        (greenSum.toLong() shl 8) or (blueSum.toLong() shl 0)).toInt()
            }
        }

        newBitmap.setPixels(newBitmapPixelsArray, 0, width / 2, 0, 0, width / 2, height / 2)
        mainImage.setImageBitmap(newBitmap)
        Toast.makeText(context, "${newBitmap.width} x ${newBitmap.height}", Toast.LENGTH_SHORT).show()
    }

    fun scale2x(context: Context, mainImage: ImageView) {
        try {
            val oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap
            val height = oldBitmap.height
            val width = oldBitmap.width
            if (width * height < 3000 * 3000) {
                val oldBitmapPixelsArray = IntArray(width * height)
                val newBitmap = Bitmap.createBitmap(width * 2, height * 2, Bitmap.Config.ARGB_8888)
                val newBitmapPixelsArray = IntArray(width * 2 * height * 2)
                oldBitmap.getPixels(oldBitmapPixelsArray, 0, width, 0, 0, width, height)

                for (i in oldBitmapPixelsArray.indices) {
                    newBitmapPixelsArray[i * 2 + (i / width) * width * 2] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 2 + (i / width) * width * 2 + 1] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 2 + (i / width) * width * 2 + width * 2] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 2 + (i / width) * width * 2 + width * 2 + 1] = oldBitmapPixelsArray[i]

                    // smoothing
                    if (i >= width && i <= width * (height - 1) && i % width != 0 && i % width != width - 1) {
                        val a = oldBitmapPixelsArray[i - width]
                        val b = oldBitmapPixelsArray[i + 1]
                        val c = oldBitmapPixelsArray[i - 1]
                        val d = oldBitmapPixelsArray[i + width]

                        if (c == a && c != d && a != b) {
                            newBitmapPixelsArray[i * 2 + (i / width) * width * 2] = a
                        }
                        if (a == b && a != c && b != d) {
                            newBitmapPixelsArray[i * 2 + (i / width) * width * 2 + 1] = b
                        }
                        if (d == c && d != b && c != a) {
                            newBitmapPixelsArray[i * 2 + (i / width) * width * 2 + width * 2] = c
                        }
                        if (b == d && b != a && d != c) {
                            newBitmapPixelsArray[i * 2 + (i / width) * width * 2 + width * 2 + 1] = d
                        }
                    }
                }

                newBitmap.setPixels(newBitmapPixelsArray, 0, width * 2, 0, 0, width * 2, height * 2)
                mainImage.setImageBitmap(newBitmap)
                Toast.makeText(context, "${newBitmap.width} x ${newBitmap.height}", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(context, "The image is too large", Toast.LENGTH_SHORT).show()
        }
        catch (e: OutOfMemoryError) {
            Toast.makeText(context, "The image is too large", Toast.LENGTH_SHORT).show()
        }
    }

    fun scale3x(context: Context, mainImage: ImageView) {
        try {
            val oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap
            val height = oldBitmap.height
            val width = oldBitmap.width
            if (width * height < 2000 * 2000) {
                val oldBitmapPixelsArray = IntArray(width * height)
                val newBitmap = Bitmap.createBitmap(width * 3, height * 3, Bitmap.Config.ARGB_8888)
                val newBitmapPixelsArray = IntArray(width * 3 * height * 3)
                oldBitmap.getPixels(oldBitmapPixelsArray, 0, width, 0, 0, width, height)

                for (i in oldBitmapPixelsArray.indices) {
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + 1] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + 2] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 + 1] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 + 2] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 * 2] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 * 2 + 1] = oldBitmapPixelsArray[i]
                    newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 * 2 + 2] = oldBitmapPixelsArray[i]

                    // smoothing
                    if (i >= width && i <= width * (height - 1) && i % width != 0 && i % width != width - 1) {
                        val a = oldBitmapPixelsArray[i - width - 1]
                        val b = oldBitmapPixelsArray[i - width]
                        val c = oldBitmapPixelsArray[i - width + 1]
                        val d = oldBitmapPixelsArray[i - 1]
                        val e = oldBitmapPixelsArray[i]
                        val f = oldBitmapPixelsArray[i + 1]
                        val g = oldBitmapPixelsArray[i + width - 1]
                        val h = oldBitmapPixelsArray[i + width]
                        val j = oldBitmapPixelsArray[i + width + 1]

                        if (d == b && d != h && b != f) {
                            newBitmapPixelsArray[i * 3 + (i / width) * width * 6] = d
                        }
                        if ((d == b && d != h && b != f && e != c) ||
                            (b == f && b != d && f != h && e != a)
                        ) {
                            newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + 1] = b
                        }
                        if (b == f && b != d && f != h) {
                            newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + 2] = f
                        }
                        if ((h == d && h != f && d != b && e != a) ||
                            (d == b && d != h && b != f && e != g)
                        ) {
                            newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3] = d
                        }
                        if ((b == f && b != d && f != h && e != j) ||
                            (f == h && f != b && h != d && e != c)
                        ) {
                            newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 + 2] = f
                        }
                        if (h == d && h != f && d != b) {
                            newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 * 2] = d
                        }
                        if ((f == h && f != b && h != d && e != g) ||
                            (h == d && h != f && d != b && e != j)
                        ) {
                            newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 * 2 + 1] = h
                        }
                        if (f == h && f != b && h != d) {
                            newBitmapPixelsArray[i * 3 + (i / width) * width * 6 + width * 3 * 2 + 2] = f
                        }
                    }
                }

                newBitmap.setPixels(newBitmapPixelsArray, 0, width * 3, 0, 0, width * 3, height * 3)
                mainImage.setImageBitmap(newBitmap)
                Toast.makeText(context, "${newBitmap.width} x ${newBitmap.height}", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(context, "The image is too large", Toast.LENGTH_SHORT).show()
        }
        catch (e: OutOfMemoryError) {
            Toast.makeText(context, "The image is too large", Toast.LENGTH_SHORT).show()
        }
    }

    // CALL FILTERS FUNCTION
    fun filter(mainImage: ImageView, number : Int){
        val oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap
        backBitmap = oldBitmap
        val height = oldBitmap.height
        val width = oldBitmap.width
        val oldBittmapPixelsArray = IntArray(width * height)
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        oldBitmap.getPixels(oldBittmapPixelsArray, 0, width, 0, 0, width, height)

        // CALL FILTER
        if (number == 1) {negative(oldBittmapPixelsArray, oldBittmapPixelsArray)}
        if (number == 2) {whiteBlack(oldBittmapPixelsArray, oldBittmapPixelsArray)}
        if (number == 3) {red(oldBittmapPixelsArray, oldBittmapPixelsArray)}

        newBitmap.setPixels(oldBittmapPixelsArray, 0, width, 0, 0, width, height)
        mainImage.setImageBitmap(newBitmap)
    }

    // FILTERS BEGIN
    private fun negative(oldBitmapPixelsArray: IntArray, newBitmapPixelsArray: IntArray){
        for (i in oldBitmapPixelsArray.indices) {
            newBitmapPixelsArray[i] = (0xff000000 or (0xffffffff - oldBitmapPixelsArray[i])).toInt()
        }
    }
    private fun red(oldBitmapPixelsArray: IntArray, newBitmapPixelsArray: IntArray){
        for (i in oldBitmapPixelsArray.indices){
            val red = (oldBitmapPixelsArray[i] and 0x00ff0000 shr 16) // 8 0 shl
            newBitmapPixelsArray[i] = ((0xff000000) or (red.toLong() shl 16)).toInt()
        }
    }
    private fun whiteBlack(oldBitmapPixelsArray: IntArray, newBitmapPixelsArray: IntArray){
        for (i in oldBitmapPixelsArray.indices){
            val red = (oldBitmapPixelsArray[i] and 0x00ff0000 shr 16) // 8 0 shl
            val green = (oldBitmapPixelsArray[i] and 0x0000ff00 shr 8)
            val blue = (oldBitmapPixelsArray[i] and 0x000000ff shr 0)

            val averageColor = (red + green + blue) / 3
            newBitmapPixelsArray[i] = ((0xff000000) or (averageColor.toLong() shl 16) or (averageColor.toLong() shl 8) or (averageColor.toLong() shl 0)).toInt()
        }
    }
    // FILTERS END

    // Fucking rotation doesn't work
    fun rotateImage(mainImage: ImageView, context: Context){
        try {
            val oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap // создаем битмап из imageview
            var height = oldBitmap.height // высота картинки и битмапа
            var width = oldBitmap.width // ширина
            val oldBittmapPixelsArray =
                IntArray(width * height) // массив его пикселей (пока просто массив, не двумерный, и пока он пустой, то есть ничего не содержит, т.е. пока это просто массив длиной width * height)
            val newBitmap = Bitmap.createBitmap(
                height,
                width,
                Bitmap.Config.ARGB_8888
            )  // ноздаем новый битмап (пока пустой, но шириной и высотой такой же, как и прошлый)
            oldBitmap.getPixels(
                oldBittmapPixelsArray,
                0,
                width,
                0,
                0,
                width,
                height
            ) // заполняем старый массив пикселей пикселями из старого битмапа
// а тут мы первращаем массив пикселей в матрицу пикселей
            val matrix = Array(height) { IntArray(width) } //будущая матрица
            var count = 0
            for (i in matrix.indices) {
                for (j in 0 until matrix[i].size) {
                    matrix[i][j] = oldBittmapPixelsArray[count++] //перенос элементов из донора в матрицу
                }
            }
            val newMatrix = Array(width) { IntArray(height) }

            for (rw in 0 until height)
                for (cl in 0 until width) {
                    newMatrix[width - 1 - cl][rw] = matrix[rw][cl]
                }

            val tmp: Int
            tmp = height
            height = width
            width = tmp

            for (row in 0 until height) {
                for (column in 0 until width) {
                    oldBittmapPixelsArray[(row * width) + column] =
                        newMatrix[row][column] // from matrix to new empty pixels array
                }
            }
            newBitmap.setPixels(oldBittmapPixelsArray, 0, width, 0, 0, width, height) // здесь вылет
            mainImage.setImageBitmap(newBitmap)
        }
        catch(e:OutOfMemoryError){
            Toast.makeText(context, "Applied", Toast.LENGTH_SHORT).show()
        }
    }

    // DAMN BLUR
    @SuppressLint("ClickableViewAccessibility")
    fun blur(mainImage: ImageView) {
        mainImage.setOnTouchListener(View.OnTouchListener { _, event ->
            // click coordinates
            val rawX = event.x
            val rawY = event.y

            val oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap
            val height = oldBitmap.height
            val width = oldBitmap.width
            // numberGet bitmap coordinates
            val x = (rawX.toDouble() * (width.toDouble() / mainImage.width.toDouble())).toInt()
            val y = (rawY.toDouble() * (height.toDouble() / mainImage.height.toDouble())).toInt()

//            coordinates.text = "$x | $y"
            val oldBittmapPixelsArray = IntArray(width * height) // empty pixels array
            val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) // new bitmap
            // oldBittmapPixelsArray // the same for new bitmap
            oldBitmap.getPixels(oldBittmapPixelsArray, 0, width, 0, 0, width, height) // filling pixels
            var count = 0
            val matrix = Array(height) { IntArray(width) } //будущая матрица
            for (i in matrix.indices) {
                for (j in 0 until matrix[i].size) {
                    matrix[i][j] = oldBittmapPixelsArray[count++] //перенос элементов из донора в матрицу
                }
            }
            var redSum = 0
            var greenSum = 0
            var blueSum = 0

            val eps = ((sqrt(height.toDouble() * width.toDouble())) / (100)).toInt() // ЭТО ОКРЕСТНОСТЬ БЛЮРА (тип радиус), КОРОЧЕ НАДО ПРИДУМАТЬ, как ее менять в зависимости от разрешения

            for (i in y - eps..y + eps){
                for (j in x - eps..x + eps){
                    if (i in 0..(height - 1) && j >= 0 && j < width){
                        val red: Int = matrix[i][j] and 0x00ff0000 shr 16
                        val green: Int = matrix[i][j] and 0x0000ff00 shr 8
                        val blue: Int = matrix[i][j] and 0x000000ff shr 0
                        redSum += red
                        greenSum += green
                        blueSum += blue
                    }
                }
            }
            redSum /= ((1 + 2 * eps) * (1 + 2 * eps))
            greenSum /= ((1 + 2 * eps) * (1 + 2 * eps))
            blueSum /= ((1 + 2 * eps) * (1 + 2 * eps))
            for (i in y - eps..y + eps){
                for (j in x - eps..x + eps){
                    if (i in 0..(height - 1) && j >= 0 && j < width){
                        matrix[i][j] = ((0xff000000) or (redSum.toLong() shl 16) or (greenSum.toLong() shl 8) or (blueSum.toLong() shl 0)).toInt()
                    }
                }
            }

            for (row in 0 until height){
                for (column in 0 until width) {
                    oldBittmapPixelsArray[(row * width) + column] = matrix[row][column]
                }
            }
            newBitmap.setPixels(oldBittmapPixelsArray, 0, width, 0, 0, width, height)
            mainImage.setImageBitmap(newBitmap)

            return@OnTouchListener true
        })
    }

    fun unsharpMask(context: Context, mainImage: ImageView) {
        val oldBitmap = (mainImage.drawable as BitmapDrawable).bitmap
        val height = oldBitmap.height
        val width = oldBitmap.width
        val oldBitmapPixelsArray = IntArray(width * height)
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val newBitmapPixelsArray = IntArray(width * height)
        oldBitmap.getPixels(oldBitmapPixelsArray, 0, width, 0, 0, width, height)

        val ys: IntArray = intArrayOf(-width, 0, width)
        val xs: IntArray = intArrayOf(-1, 0, +1)

        for (i in oldBitmapPixelsArray.indices) {
            var redSum = 0
            var greenSum = 0
            var blueSum = 0

            for (j in 0 until 3) {
                for (k in 0 until 3) {
                    val red = if (i + ys[j] + xs[k] >= 0 && i + ys[j] + xs[k] < width * height) {
                        oldBitmapPixelsArray[i + ys[j] + xs[k]] and 0x00ff0000 shr 16
                    } else {
                        0
                    }
                    val green = if (i + ys[j] + xs[k] >= 0 && i + ys[j] + xs[k] < width * height) {
                        oldBitmapPixelsArray[i + ys[j] + xs[k]] and 0x0000ff00 shr 8
                    } else {
                        0
                    }
                    val blue = if (i + ys[j] + xs[k] >= 0 && i + ys[j] + xs[k] < width * height) {
                        oldBitmapPixelsArray[i + ys[j] + xs[k]] and 0x000000ff shr 0
                    } else {
                        0
                    }

                    redSum += red
                    greenSum += green
                    blueSum += blue
                }
            }

            redSum /= 9
            greenSum /= 9
            blueSum /= 9

            newBitmapPixelsArray[i] = ((0xff000000) or (redSum.toLong() shl 16) or
                    (greenSum.toLong() shl 8) or (blueSum.toLong() shl 0)).toInt()

            val redOld = oldBitmapPixelsArray[i] and 0x00ff0000 shr 16
            val greenOld = oldBitmapPixelsArray[i] and 0x0000ff00 shr 8
            val blueOld = oldBitmapPixelsArray[i] and 0x000000ff shr 0

            val redNew = newBitmapPixelsArray[i] and 0x00ff0000 shr 16
            val greenNew = newBitmapPixelsArray[i] and 0x0000ff00 shr 8
            val blueNew = newBitmapPixelsArray[i] and 0x000000ff shr 0

            newBitmapPixelsArray[i] = ((0xff000000) or (
                    (min(redOld.toLong() + (max(redOld.toLong() - redNew.toLong(), 0)), 255)) shl 16) or
                    (min(greenOld.toLong() + (max(greenOld.toLong() - greenNew.toLong(), 0)), 255) shl 8) or
                    (min(blueOld.toLong() + (max(blueOld.toLong() - blueNew.toLong(), 0)), 255) shl 0)).toInt()
        }

        newBitmap.setPixels(newBitmapPixelsArray, 0, width, 0, 0, width, height)
        mainImage.setImageBitmap(newBitmap)
        Toast.makeText(context, "Applied", Toast.LENGTH_SHORT).show()
    }
}
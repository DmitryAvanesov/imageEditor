package com.example.myapplication

import android.view.View
import android.graphics.Matrix
import android.graphics.Bitmap
import android.R
import android.graphics.BitmapFactory





import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.math.min



class MainActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private val IMAGE_PICK_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var image_uri: Uri? = null
    private var REQ_CODE_FOR_ACTION: Int = 0
    private val edit = EditImage()
    private var initialHeight = 0

    fun turnright(view: View){
        val matrix = Matrix()

        matrix.postRotate(-90F)

        val bitmapOrg = (mainImage.drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.width, bitmapOrg.height, true)

        val rotatedBitmap =
            Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        mainImage.setImageBitmap(rotatedBitmap)
    }
    fun turnleft(view: View){
        val matrix = Matrix()

        matrix.postRotate(90F)

        val bitmapOrg = (mainImage.drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.width, bitmapOrg.height, true)

        val rotatedBitmap =
            Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        mainImage.setImageBitmap(rotatedBitmap)
    }
//    private val image = OpenImage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //button click
        imageButton.setOnClickListener {
            checkPermissionsForGallery()
        }
        cameraButton.setOnClickListener {
            checkPermissionsForCamera()
        }

        @Suppress("DEPRECATION")
        mainImage.layoutParams.width = windowManager.defaultDisplay.width / 2
        @Suppress("DEPRECATION")
        mainImage.layoutParams.height = windowManager.defaultDisplay.height / 2

        initialHeight = mainImage.layoutParams.height

        // 100% scale by default
        scaleSpinner.setSelection(2)
    }

    // SO NIGGAS THAT'S MY FUCKING CHECK FOR PERMISSIONS OK?
    private fun checkPermissionsForGallery(){
        // FOR GALLERY REQ CODE
        REQ_CODE_FOR_ACTION = 2002

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else{
                //permission already granted
                pickImageFromGallery()
            }
        }
        else{
            //system OS is < Marshmallow
            pickImageFromGallery()
        }
    }

    private fun checkPermissionsForCamera(){
        // FOR CAMERA REQ CODE
        REQ_CODE_FOR_ACTION = 2001

        //if system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else{
                //permission already granted
                openCamera()
            }
        }
        else{
            //system os is < marshmallow
            openCamera()
        }
    }

    // SOME KIND OF PERMISSIONS CODE SHIT
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && REQ_CODE_FOR_ACTION == 2001) {
                    //permission from popup was granted
                    openCamera()
                }
                else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && REQ_CODE_FOR_ACTION == 2002) {
                    //permission from popup was granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // OPEN YOUR CAMERA NIGGA
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    // OPEN YOUR GALLERY NIGGA
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // SAVE YOUR ASS IMAGE NIGGA
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            mainImage.setImageURI(data?.data)
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
            //set image captured to image view
            mainImage.setImageURI(image_uri)
        }

        compressImage()
    }

    // IMAGE COMPRESSION
    private fun compressImage() {
        val reqHeight = 1500
        val reqWidth = 1500
        val compressCoefficient: Double
        var bitmap = (mainImage.drawable as BitmapDrawable).bitmap

        if (bitmap.width > reqWidth || bitmap.height > reqHeight) {
            compressCoefficient = min(bitmap.width.toDouble() / reqWidth.toDouble(),
                bitmap.height.toDouble() / reqHeight.toDouble())

            if (compressCoefficient > 1) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width.toDouble() / compressCoefficient).toInt(),
                    (bitmap.height.toDouble() / compressCoefficient).toInt(), false)
                mainImage.setImageBitmap(bitmap)
            }
        }
    }

    fun saveImage(@Suppress("UNUSED_PARAMETER") view: View) {
        val root = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}" +
                "${File.separator}imageEditor${File.separator}")
        root.mkdir()
    }

    // JUST A WRAPPER FUNCTION FOR SCALING
    fun getScale(@Suppress("UNUSED_PARAMETER") view: View) {
        edit.scale(mainImage, scaleSpinner.selectedItem.toString(), initialHeight)
    }

    // DMITRY'S FUNCTION TO FUCK SOME BITCHES USING A*
    fun transitToAStar(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, AStarMainActivity::class.java)
        startActivity(intent)
    }

}
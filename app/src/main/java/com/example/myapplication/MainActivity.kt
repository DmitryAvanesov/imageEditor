package com.example.myapplication


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
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
import java.io.FileOutputStream
import java.text.DateFormat.getDateTimeInstance
import java.util.*


class MainActivity : AppCompatActivity() {

    private val permissionCode = 1000
    private val imagePickCode = 1000
    private val imageCaptureCode = 1001
    private var imageURI: Uri? = null
    private var reqCodeForAction: Int = 0
    private var initialHeight = 0
    private var returnBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    private var backBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    private val edit = EditImage(backBitmap)
    private val bil = BilinearFilter()
    private val actionBut = ActionsWithButtons()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //button click
        imageButton.setOnClickListener { checkPermissionsForGallery() }
        cameraButton.setOnClickListener { checkPermissionsForCamera() }

        @Suppress("DEPRECATION")
        mainImage.layoutParams.width = windowManager.defaultDisplay.width / 2
        @Suppress("DEPRECATION")
        mainImage.layoutParams.height = windowManager.defaultDisplay.height / 2

        initialHeight = mainImage.layoutParams.height

        // 100% enlarge by default
        zoomSpinner.setSelection(2)

        // SHOW FILTERS BUTTONS
        toolsButton.setOnClickListener{ actionBut.filterClick(firstFilter, secondFilter, thirdFilter,
            cameraButton, imageButton, backButton, returnButton, rotateButton, saveButton, zoomButton,
            unsharpMaskButton, blurButton, scaleButton, algorithmsButtom, zoomSpinner, scaleSpinner, bfButton, coefficient) }
        // NEGATIVE FILTER
        firstFilter.setOnClickListener { edit.filter(mainImage, 1) }
        secondFilter.setOnClickListener { edit.filter(mainImage, 2) }
        thirdFilter.setOnClickListener { edit.filter(mainImage, 3) }
        var blurCounter = 0
        blurButton.setOnClickListener {
            if (blurCounter % 2 == 0) {
                Toast.makeText(this, "Blur activated", Toast.LENGTH_SHORT).show()
                edit.blur(mainImage)
            }
            else {
                Toast.makeText(this, "Blur disabled", Toast.LENGTH_SHORT).show()
                mainImage.setOnTouchListener(null)
            }
            blurCounter++
        }
        unsharpMaskButton.setOnClickListener { edit.unsharpMask(this, mainImage, coefficient) }
        returnButton.setOnClickListener { edit.returnImage(mainImage, returnBitmap) }
        backButton.setOnClickListener {
            backBitmap = edit.returnBackBitmap()
            edit.returnImage(mainImage, backBitmap)
        }
        rotateButton.setOnClickListener { edit.rotateImage(mainImage, this) }
        var bilCounter = 0
        bfButton.setOnClickListener {
            if (bilCounter % 2 == 0) {
                bil.pickPoints(mainImage, this)
                Toast.makeText(this, "BF activated", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "BF disabled", Toast.LENGTH_SHORT).show()
                mainImage.setOnTouchListener(null)
            }
            bilCounter++
        }

        zoomSpinner.visibility = View.INVISIBLE
        scaleSpinner.visibility = View.INVISIBLE
        coefficient.visibility = View.INVISIBLE
    }

    // SO GUYS THAT'S MY F*CKING CHECK FOR PERMISSIONS OK?
    private fun checkPermissionsForGallery(){
        // FOR GALLERY REQ CODE
        reqCodeForAction = 2002

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, permissionCode)
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
        reqCodeForAction = 2001

        //if system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, permissionCode)
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

    // SOME KIND OF PERMISSIONS CODE
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            permissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && reqCodeForAction == 2001) {
                    //permission from popup was granted
                    openCamera()
                }
                else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && reqCodeForAction == 2002) {
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

    // OPEN YOUR CAMERA
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageURI = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
        startActivityForResult(cameraIntent, imageCaptureCode)
    }

    // OPEN YOUR GALLERY
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imagePickCode)
    }

    // SAVE YOUR ASS IMAGE
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK && requestCode == imagePickCode){
            mainImage.setImageURI(data?.data)
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == imageCaptureCode){
            //set image captured to image view
            mainImage.setImageURI(imageURI)
        }

        returnBitmap = (mainImage.drawable as BitmapDrawable).bitmap

        // SHOW BUTTON "FILTERS":
        toolsButton.visibility = View.VISIBLE
    }

    // SAVING YOUR EDITED IMAGE TO A SPECIFIC FOLDER
    fun saveImage(@Suppress("UNUSED_PARAMETER") view: View) {
        // Creating new folder
        val folder = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}" +
                "${File.separator}imageEditor${File.separator}")
        folder.mkdir()

        val dateFormat = getDateTimeInstance()
        val currentDate = dateFormat.format(Date())
        val file = File(folder, "Img[$currentDate].jpg")
        val outputStream = FileOutputStream(file)
        val bitmap = (mainImage.drawable as BitmapDrawable).bitmap

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

        // Forcing the system to see your new image
        val folderArray = arrayOf(folder.path)
        val fileArray = arrayOf(file.path)
        MediaScannerConnection.scanFile(this, folderArray, fileArray, null)
    }

    // JUST A WRAPPER FUNCTION FOR ENLARGING
    fun doZooming(@Suppress("UNUSED_PARAMETER") view: View) {
        edit.enlarge(mainImage, zoomSpinner.selectedItem.toString(), initialHeight)
    }

    // JUST A WRAPPER FUNCTION FOR ENLARGING
    fun doScaling(@Suppress("UNUSED_PARAMETER") view: View) {
        when {
            scaleSpinner.selectedItemPosition == 0 -> edit.scale05x(this, mainImage)
            scaleSpinner.selectedItemPosition == 1 -> edit.scale2x(this, mainImage)
            scaleSpinner.selectedItemPosition == 2 -> edit.scale3x(this, mainImage)
        }
    }

    // TRANSITION TO THE ACTIVITY WHERE YOU CAN CHOOSE ONE OF 2 ALGORITHMS
    fun transitToAlgorithms(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, AlgorithmChoiceActivity::class.java)
        startActivity(intent)
    }
}
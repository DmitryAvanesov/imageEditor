package com.example.myapplication

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner

class ActionsWithButtons {
    fun filterClick(firstFilterButton : Button, secondFilterButton: Button, ThirdFilterButton: Button,
                    cameraButton: ImageButton, imageButton: ImageButton, backButton: Button,
                    returnButton: Button, rotateButton: Button, saveButton: Button, zoomButton: Button,
                    unsharpMaskButton: Button, blurButton: Button, scaleButton: Button, algorithmsButton: Button,
                    zoomSpinner: Spinner, scaleSpinner: Spinner, button: Button){
        if (firstFilterButton.visibility == View.INVISIBLE){
            cameraButton.visibility = View.INVISIBLE
            imageButton.visibility = View.INVISIBLE
            algorithmsButton.visibility = View.INVISIBLE
            button.visibility = View.INVISIBLE
            firstFilterButton.visibility = View.VISIBLE
            secondFilterButton.visibility = View.VISIBLE
            ThirdFilterButton.visibility = View.VISIBLE
            backButton.visibility = View.VISIBLE
            returnButton.visibility = View.VISIBLE
            rotateButton.visibility = View.VISIBLE
            saveButton.visibility = View.VISIBLE
            zoomButton.visibility = View.VISIBLE
            unsharpMaskButton.visibility = View.VISIBLE
            blurButton.visibility = View.VISIBLE
            scaleButton.visibility = View.VISIBLE
            zoomSpinner.visibility = View.VISIBLE
            scaleSpinner.visibility = View.VISIBLE
        }
        else {
            button.visibility = View.VISIBLE
            firstFilterButton.visibility = View.INVISIBLE
            secondFilterButton.visibility = View.INVISIBLE
            ThirdFilterButton.visibility = View.INVISIBLE
            backButton.visibility = View.INVISIBLE
            returnButton.visibility = View.INVISIBLE
            rotateButton.visibility = View.INVISIBLE
            saveButton.visibility = View.INVISIBLE
            zoomButton.visibility = View.INVISIBLE
            unsharpMaskButton.visibility = View.INVISIBLE
            blurButton.visibility = View.INVISIBLE
            scaleButton.visibility = View.INVISIBLE
            zoomSpinner.visibility = View.INVISIBLE
            scaleSpinner.visibility = View.INVISIBLE
            cameraButton.visibility = View.VISIBLE
            imageButton.visibility = View.VISIBLE
            algorithmsButton.visibility = View.VISIBLE
        }
    }
}
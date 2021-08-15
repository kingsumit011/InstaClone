package com.android.example.instaclone.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.*

class ImageManager:AppCompatActivity {

    var mProfileUri: Uri? = null
    var mimage: ImageView? =null
        
    var mcontext: Context? = null

    constructor(activity: Activity , context:Context ,imageView: ImageView  ){
        mimage = imageView
        ImagePicker.with(activity)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }

    }

    private val startForProfileImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    mProfileUri = fileUri

                    mimage!!.setImageURI(fileUri)

                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(mcontext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mcontext, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }

}




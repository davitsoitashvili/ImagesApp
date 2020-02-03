package com.example.designapp

import android.os.Build.*
import android.content.pm.PackageManager
import android.os.Build
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothClass
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.items.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException

@Suppress(
    "DEPRECATION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class MainActivity : AppCompatActivity(), ItemClickListener {

    val imageArray = mutableListOf<Bitmap>()
    var adapter: RecyclerAdapter = RecyclerAdapter(imageArray, this)
    val values = ContentValues()
    var imageUri: Uri? = null

    @RequiresApi(VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setRecyclerViewAdapter()
        checkWriteInStoragePermission()
        checkCameraPermission(mainActivityTakeImage)
        checkGalleryPermission(mainActivityUploadImage)

    }

    private fun setRecyclerViewAdapter() {
        val recyclerView: RecyclerView = recycler_view
        recyclerView.setLayoutManager(GridLayoutManager(this, 3))
        recyclerView.adapter = adapter
    }

    override fun getItemPosition(position: Int) {
        fragmentInstance(position)

    }

    override fun deleteItem(position: Int) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
            .setMessage("Are you sure to delete image?")
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    deleteImage(position)
                    imageArray.clear()
                    adapter.notifyDataSetChanged()
                    displayImages(true)
                }
            })
            .setNegativeButton("No",null)

        val alertDialog: AlertDialog = alert.create()
        alertDialog.show()
    }

    private fun checkPermission(permission: String, code: Int, function: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(permission)
                requestPermissions(permissions, code)
            } else {
                function()
            }
        } else {
            function()
        }
    }

    private fun checkWriteInStoragePermission() {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGEWRITEREQUEST_CODE){displayImages(true)}
    }

    private fun checkCameraPermission(takeImageBtn: ImageView) {
        takeImageBtn.setOnClickListener {
            checkPermission(Manifest.permission.CAMERA, REQUEST_CAMERA_CODE, ::openCamera)
        }
    }

    @RequiresApi(VERSION_CODES.JELLY_BEAN)
    private fun checkGalleryPermission(uploadImageBtn: ImageView) {
        uploadImageBtn.setOnClickListener {
            checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                REQUEST_GALLERY_CODE,
                ::pickFromGallery
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == REQUEST_GALLERY_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromGallery()
            } else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == STORAGEWRITEREQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish()
            } else {
                displayImages(true)
            }
        }
    }


    private fun openCamera() {
        values.put(MediaStore.Images.Media.TITLE, "New picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Picture from Camera")
        imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )

        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(camera, REQUEST_CODE)

    }

    private fun pickFromGallery() {
        val gallery = Intent()
        gallery.setType("image/*")
        gallery.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(
            Intent.createChooser(gallery, "Choose image"),
            GALLERY_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            try {
                val photo = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                saveImage(photo)
                displayImages(false)


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            val imageUris = data?.data
            try {
                val photoGallery =
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUris) as Bitmap
                saveImage(photoGallery)
                displayImages(false)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun saveImage(photoImage: Bitmap) {
        val path = Environment.getExternalStorageDirectory()
        val dir = File("${path.absolutePath}/IMAGES/")
        dir.mkdirs()
        val img = File(dir, "${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(img)
        photoImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        outputStream.flush()
        outputStream.close()

    }

    private fun displayImages(reload: Boolean? = null) {
        val path = Environment.getExternalStorageDirectory()
        if (reload == true) {
            val allItems = File("${path.absolutePath}/IMAGES/").walk().forEach {
                if (BitmapFactory.decodeFile(it.absolutePath) != null) {
                    imageArray.add(BitmapFactory.decodeFile(it.absolutePath))
                    adapter.notifyDataSetChanged()
                }
            }

        } else {
            val lastItem = File("${path!!.absolutePath}/IMAGES/").walk().last()
            if (BitmapFactory.decodeFile(lastItem.absolutePath) != null) {
                imageArray.add(BitmapFactory.decodeFile(lastItem.absolutePath))
                adapter.notifyDataSetChanged()
            }
        }
    }


    private fun deleteImage(index: Int) {
        try {
            val path = Environment.getExternalStorageDirectory()
            val dir = File("${path.absolutePath}/IMAGES/").listFiles()
            dir.get(index).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fragmentInstance(position: Int) {
        try {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.addToBackStack("back")
            transaction.replace(
                R.id.ImageFragmentContainer,
                ImageFragment.isInstance(imageArray[position])
            )
            transaction.commit()
        } catch (error: RuntimeException) {
            error.printStackTrace()
        }
    }

    companion object {
        val REQUEST_CAMERA_CODE = 200
        val REQUEST_GALLERY_CODE = 210
        val REQUEST_CODE = 100
        val GALLERY_CODE = 300
        val STORAGEWRITEREQUEST_CODE = 400
    }
}
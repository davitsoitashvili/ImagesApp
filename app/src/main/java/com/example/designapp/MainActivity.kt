package com.example.designapp

import android.os.Build.*
import android.content.pm.PackageManager
import android.os.Build
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
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

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    val imageArray = mutableListOf<Bitmap>()
    var adapter: RecyclerAdapter = RecyclerAdapter(imageArray, this)

    @RequiresApi(VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView: RecyclerView = recycler_view
        recyclerView.setLayoutManager(GridLayoutManager(this, 3))
        recyclerView.adapter = adapter


        checkWriteInStoragePermission()
        checkPermissions(take_image_id)
        checkPermissions(upload_image_id)

    }

    val getPosition = { position: Int -> fragmentInstance(position) }

    private fun checkWriteInStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, 2000)
            }
        }

    }


    private fun checkCameraPermission(takeImageBtn: ImageView) {
        takeImageBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(
                        Manifest.permission.CAMERA
                    )
                    requestPermissions(permissions, REQUEST_CAMERA_CODE)

                } else {
                    openCamera()

                }
            } else {
                openCamera()
            }
        }
    }

    private fun checkGalleryPermission(uploadImageBtn: ImageView) {
        uploadImageBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                    val permissions = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    requestPermissions(permissions, REQUEST_GALLERY_CODE)

                } else {
                    pickFromGallery()

                }
            } else {
                pickFromGallery()
            }
        }
    }

    private fun checkPermissions(btn: ImageView) {

        if (btn == take_image_id) {
            checkCameraPermission(btn)
        } else {
            checkGalleryPermission(btn)
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

        if (requestCode == 2000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {

        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
                val photo = data?.extras?.get("data") as Bitmap
                saveImage(photo)
                imageArray.add(photo)
                adapter.notifyDataSetChanged()


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
                imageArray.add(photoGallery)
                adapter.notifyDataSetChanged()


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveImage(photoImage : Bitmap) {
        val path = Environment.getExternalStorageDirectory()
        val dir = File("${path.absolutePath}/IMAGES/")
        dir.mkdirs()
        val img = File(dir, "${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(img)
        photoImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        Toast.makeText(this, "The image is saved in internal storage!", Toast.LENGTH_LONG).show()
        outputStream.flush()
        outputStream.close()

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
    }
}

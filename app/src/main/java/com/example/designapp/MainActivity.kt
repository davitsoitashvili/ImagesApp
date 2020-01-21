package com.example.designapp

import android.os.Build.*
import android.content.pm.PackageManager
import android.os.Build
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.io.IOException


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    val imageArray = mutableListOf<Bitmap>()
    var adapter : RecyclerAdapter = RecyclerAdapter(imageArray)

    @RequiresApi(VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView : RecyclerView = recycler_view
        recyclerView.setLayoutManager(GridLayoutManager(this, 3))
        recyclerView.adapter = adapter

        checkPermissions()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISION_CODE)
            } else {
                openCamera()
                pickFromGallery()
            }
        }
        else {
            openCamera()
            pickFromGallery()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
                pickFromGallery()

            } else {
                Toast.makeText(this,"Permissions denied!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        take_image_id.setOnClickListener {
            val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera, REQUEST_CODE)
        }

    }

    private fun pickFromGallery() {
        upload_image_id.setOnClickListener {
            val gallery = Intent()
            gallery.setType("image/*")
            gallery.setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(gallery, "Choose image"), GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {

            val photo = data?.getExtras()?.get("data") as Bitmap
            imageArray.add(photo)
            adapter.notifyDataSetChanged()
        }

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val  imageUris  = data?.data
            try {
                val photoGallery = MediaStore.Images.Media.getBitmap(contentResolver,imageUris) as Bitmap
                imageArray.add(photoGallery)
                adapter.notifyDataSetChanged()

            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        val PERMISION_CODE = 200
        val REQUEST_CODE = 100
        val GALLERY_REQUEST_CODE = 300
    }

}

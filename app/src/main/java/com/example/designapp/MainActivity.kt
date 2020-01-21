package com.example.designapp

import android.os.Build.*
import android.content.pm.PackageManager
import android.os.Build
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*



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
        take_image_id.setOnClickListener() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                    val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISION_CODE)
                } else {
                    takePhoto()
                }
            }
            else {
                takePhoto()
            }
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
                takePhoto()

            } else {
                Toast.makeText(this,"Permissions denied!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun takePhoto() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera, REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val photo = data?.getExtras()?.get("data") as Bitmap
        imageArray.add(photo)
        adapter?.notifyDataSetChanged()


    }

    companion object {
        val PERMISION_CODE = 200
        val REQUEST_CODE = 100
    }

}

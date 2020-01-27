package com.example.designapp


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.items.*

class ImageFragment : Fragment() {

    var image : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        image = this.arguments?.getParcelable("image")
        setImg(image)
        super.onActivityCreated(savedInstanceState)
    }


    companion object {
        fun isInstance(image : Bitmap) : Fragment {
            val bundle = Bundle()
            bundle.putParcelable("image", image)
            val fragment = ImageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun setImg(img : Bitmap?) {
        ImageFragmentImageView.setImageBitmap(img)
    }

}

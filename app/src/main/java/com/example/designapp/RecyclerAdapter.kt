package com.example.designapp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.items.view.*


class RecyclerAdapter(
    images: List<Bitmap>,
    mainActivity: MainActivity
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var images: List<Bitmap> = ArrayList<Bitmap>()
    private var mainActivity : MainActivity? = null


    init {
        this.images = images
        this.mainActivity = mainActivity

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false))
        return view
    }

    override fun getItemCount(): Int {
        return images.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageBitmap(images.get(position))
        holder.initialize(mainActivity!!.getPosition)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.itemImage
        fun initialize(position: (Int) -> Unit) {
            imageView.setOnClickListener() {
                position(adapterPosition)
            }
        }
    }


}




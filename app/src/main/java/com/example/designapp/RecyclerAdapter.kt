package com.example.designapp

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.items.view.*


class RecyclerAdapter(
    images: List<Bitmap>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var images : List<Bitmap> = ArrayList<Bitmap>()

    init {
        this.images = images


    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val image = itemView.itemImage

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.itemImage.setImageBitmap(images.get(position))
    }


}
package com.example.designapp

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.items.view.*


class RecyclerAdapter(
    images: List<Bitmap>,
    listener : OnItemClickListener
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var images: List<Bitmap> = ArrayList<Bitmap>()
    private var listener : OnItemClickListener? = null

    init {
        this.images = images
        this.listener = listener

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
        holder.initialize(listener)

    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.itemImage

        fun initialize(action: OnItemClickListener?) {
            val img = imageView
            img.setOnClickListener() {
                action?.OnItemClick(adapterPosition)
            }

        }

    }

    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }
}




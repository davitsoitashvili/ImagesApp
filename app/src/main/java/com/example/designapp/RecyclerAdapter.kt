package com.example.designapp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.items.view.*

class RecyclerAdapter(
    var images: List<Bitmap>,
    var positionCallback: ItemClickListener
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

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
        holder.initialize(positionCallback)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.itemImage
        fun initialize(action: ItemClickListener?) {

            imageView.setOnClickListener() {
                action?.getItemPosition(adapterPosition)
            }


            imageView.setOnLongClickListener {
                action?.deleteItem(adapterPosition)
                false
            }

        }
    }
}


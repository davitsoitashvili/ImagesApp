package com.example.designapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.media.tv.TvView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.items.view.*

class RecyclerAdapter (
    images: List<Bitmap>,
    positionCallback: ItemClickListener
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var images: List<Bitmap> = ArrayList<Bitmap>()
    private var positionCallback: ItemClickListener? = null


    init {
        this.images = images
        this.positionCallback = positionCallback

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false))
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
                val alert: AlertDialog.Builder = AlertDialog.Builder(CONTEXT)
                    .setTitle("Choose")
                    .setMessage("Delete or Open Image?")
                    .setPositiveButton("Open", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            action?.ItemListener(adapterPosition,false)
                        }

                    })
                    .setNegativeButton("Delete", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            action?.ItemListener(adapterPosition,true)
                        }

                    })

                val alertDialog : AlertDialog = alert.create()
                alertDialog.show()

            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var CONTEXT: Context? = null
    }

}

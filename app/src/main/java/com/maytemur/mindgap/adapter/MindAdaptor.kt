package com.maytemur.mindgap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maytemur.mindgap.R
import com.maytemur.mindgap.model.Paylasim
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*
import java.util.zip.Inflater

class MindAdaptor(val mindListesi: ArrayList<Paylasim>) :
    RecyclerView.Adapter<MindAdaptor.MindHolder>() {
    class MindHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MindHolder {
        val inflater = LayoutInflater.from(parent.context)
        val gorunum = inflater.inflate(R.layout.recycler_row, parent, false)
        return MindHolder(gorunum)
    }

    override fun onBindViewHolder(holder: MindHolder, position: Int) {
        holder.itemView.recycler_row_kullanici_adi.text = mindListesi[position].kullaniciName
        holder.itemView.recycler_row_paylasim_mesaji.text = mindListesi[position].kullaniciYorum
        if (mindListesi[position].gorselUrl != null) {
            holder.itemView.recycler_row_imageview.visibility = View.VISIBLE
            Picasso.get().load(mindListesi[position].gorselUrl)
                .into(holder.itemView.recycler_row_imageview)
        }
    }

    override fun getItemCount(): Int {
        return mindListesi.size
    }
}



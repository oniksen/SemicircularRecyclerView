package com.onixen.semicircularrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class RecyclerAdapter(
    private val list: List<String>,
    private val itemClickAction: (position: Int) -> Unit
): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var selectedItem = 0

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemText = itemView.findViewById<TextView>(R.id.itemText)!!
        val itemContainer = itemView.findViewById<MaterialCardView>(R.id.itemContainer)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            itemText.text = list[position]
            itemContainer.setOnClickListener {
               itemClickAction(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
package com.example.exem


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoritesAdapter(
    private var items: List<String>,
    private val onRemove: (String) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.Holder>() {

    fun submit(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.itemText)
        val remove: TextView = v.findViewById(R.id.removeBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return Holder(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.text.text = item
        holder.remove.setOnClickListener { onRemove(item) }
    }
}


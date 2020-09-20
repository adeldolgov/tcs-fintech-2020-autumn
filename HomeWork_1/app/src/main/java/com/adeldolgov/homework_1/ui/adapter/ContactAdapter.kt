package com.adeldolgov.homework_1.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.homework_1.data.POJO.ContactData
import com.adeldolgov.homework_1.ui.viewholder.ContactViewHolder
import java.util.*

class ContactAdapter(private val list: ArrayList<ContactData>) :
    RecyclerView.Adapter<ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ContactViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
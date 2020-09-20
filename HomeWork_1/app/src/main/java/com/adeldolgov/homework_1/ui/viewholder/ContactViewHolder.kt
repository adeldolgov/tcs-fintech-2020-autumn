package com.adeldolgov.homework_1.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.homework_1.R
import com.adeldolgov.homework_1.data.POJO.ContactData
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_contact, parent, false)) {

    fun bind(contact: ContactData) {
        with(itemView) {
            contactName.text = contact.displayName
            contactPhoneNumber.text = contact.phoneNumber
        }
    }
}
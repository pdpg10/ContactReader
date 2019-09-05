package com.example.contactreader

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contact_item.view.*

//http://hannesdorfmann.com/android/adapter-delegates

class ContactAdapter(context: Context) :
    ListAdapter<Contact, ContactAdapter.ContactVH>(ContactDiffer()) {
    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactVH {
        val v = inflater.inflate(R.layout.contact_item, parent, false)
        return ContactVH(v)
    }

    override fun onBindViewHolder(holder: ContactVH, position: Int) {
        val contact = getItem(position)
        holder.onBind(contact)
    }

    class ContactVH(v: View) : RecyclerView.ViewHolder(v) {

        fun onBind(c: Contact) {
            itemView.tv_name.text = c.name
            if (c.photo != null) {
                itemView.iv_profile.setImageURI(Uri.parse(c.photo))
            }
            if (c.numbers.isNotEmpty()) {
                itemView.tv_tel.text = c.numbers[0].number
            }
        }

    }

    class ContactDiffer : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(
            oldItem: Contact,
            newItem: Contact
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact) = oldItem == newItem

    }
}
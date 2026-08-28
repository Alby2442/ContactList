package com.example.contactlist

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private var contacts: List<Contact>
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImage: ImageView = itemView.findViewById(R.id.imageAvatar)
        val nameText: TextView = itemView.findViewById(R.id.textName)
        val phoneText: TextView = itemView.findViewById(R.id.textPhone)
        val callButton: Button = itemView.findViewById(R.id.buttonCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)

        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.avatarImage.setImageResource(contact.avatar)
        holder.nameText.text = contact.name
        holder.phoneText.text = contact.phone

        // Call button
        holder.callButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                "tel:${contact.phone}".toUri()
            )

            holder.itemView.context.startActivity(intent)
        }

        // Contact click - show details
        holder.itemView.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle(contact.name)
                .setMessage("Phone: ${contact.phone}")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun updateList(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }
}
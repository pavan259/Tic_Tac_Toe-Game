package com.pavan.tictactoe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView

import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pavan.tictactoe.models.User

/**
 * CLass COMMENT
 *
 * @see "Adapter for Recycler View to show Friends "
 */
class FriendsAdapter(private val friendsList: ArrayList<User>, private val context: Context) :
    RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_friends, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friendsList[position]
        holder.tvName.text = friend.name
        holder.tvEmail.text = friend.email
        Glide.with(context)
            .load(friend.photoUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_avatar)
            .into(holder.ivProfile)
        holder.btnInvite.setOnClickListener {
            (context as FriendsListActivity).inviteFriend(friend)
        }
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfile: AppCompatImageView = itemView.findViewById(R.id.ivProfile)
        val tvName: AppCompatTextView = itemView.findViewById(R.id.tvName)
        val tvEmail: AppCompatTextView = itemView.findViewById(R.id.tvEmail)
        val btnInvite: AppCompatButton = itemView.findViewById(R.id.btnInvite)
    }
}
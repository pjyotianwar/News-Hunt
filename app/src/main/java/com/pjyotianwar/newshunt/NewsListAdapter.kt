package com.pjyotianwar.newshunt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsListAdapter(private val newsitem : NewsItemClicked)
    :RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>() {

    private val newsItemList: ArrayList<NewsItem> = ArrayList()

    //View holder
    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newsImage: ImageView
        val newsTitle : TextView
        val newsDescription : TextView

        init {
            newsImage = view.findViewById(R.id.newsImage)
            newsTitle = view.findViewById(R.id.newsTitle)
            newsDescription = view.findViewById(R.id.newsSubTitle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item, parent, false)

        val viewHolder = NewsViewHolder(view)
        view.setOnClickListener(){
            newsitem.onItemClicked(newsItemList[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(newsItemList[position].ImageUrl)
            .fitCenter()
            .into(holder.newsImage)
        holder.newsTitle.setText(newsItemList[position].Title)
        holder.newsDescription.setText(newsItemList[position].Description)
    }

    override fun getItemCount() = newsItemList.size

    fun updateNews(updatedNews: ArrayList<NewsItem>) {
        newsItemList.clear()
        newsItemList.addAll(updatedNews)

        notifyDataSetChanged()
    }
}

interface NewsItemClicked{
    fun onItemClicked(item : NewsItem)
}
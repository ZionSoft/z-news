/*
 * Copyright (C) 2018 ZionSoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.zionsoft.news.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import net.zionsoft.news.R
import net.zionsoft.news.detail.DetailActivity
import net.zionsoft.news.misc.GlideApp
import net.zionsoft.news.model.NewsItem
import net.zionsoft.news.utils.toLocalDateTime

internal class NewsItemViewHolder(inflater: LayoutInflater, container: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_news, container, false)), View.OnClickListener {
    @BindView(R.id.title)
    internal lateinit var title: TextView

    @BindView(R.id.date)
    internal lateinit var date: TextView

    @BindView(R.id.image)
    internal lateinit var image: ImageView

    init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener(this)
    }

    fun bind(newsItem: NewsItem) {
        title.text = newsItem.title
        date.text = newsItem.published.toLocalDateTime()

        if (newsItem.enclosure != null) {
            GlideApp.with(image).load(newsItem.enclosure.url).centerCrop().into(image)
        }
    }

    override fun onClick(v: View) {
        val context = v.context
        context.startActivity(DetailActivity.newStartIntent(context))
    }
}

internal class NewsListAdapter(context: Context) : RecyclerView.Adapter<NewsItemViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var newsItems: List<NewsItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        return NewsItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) {
        holder.bind(newsItems!![position])
    }

    override fun getItemCount(): Int {
        return newsItems?.size ?: 0
    }

    fun setNewsItem(newsItems: List<NewsItem>) {
        this.newsItems = newsItems
        notifyDataSetChanged()
    }
}

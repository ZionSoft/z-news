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
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import net.zionsoft.news.R
import net.zionsoft.news.model.NewsItem

internal class NewsItemViewHolder(inflater: LayoutInflater, container: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_news, container, false)) {
    @BindView(R.id.title)
    internal lateinit var title: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun bind(newsItem: NewsItem) {
        title.text = newsItem.title
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

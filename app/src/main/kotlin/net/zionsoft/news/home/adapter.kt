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

import android.app.Activity
import android.content.Context
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
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

internal class NewsItemViewHolder(inflater: LayoutInflater, container: ViewGroup, private val listener: Listener)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_news, container, false)), View.OnClickListener {
    interface Listener {
        fun onNewsItemOpened(newsItem: NewsItem)
    }

    @BindView(R.id.title)
    internal lateinit var title: TextView

    @BindView(R.id.date)
    internal lateinit var date: TextView

    @BindView(R.id.image)
    internal lateinit var image: ImageView

    private var newsItem: NewsItem? = null

    init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener(this)
    }

    fun bind(newsItem: NewsItem, readCount: Int) {
        this.newsItem = newsItem

        title.text = newsItem.title
        title.setTextColor(ContextCompat.getColor(title.context,
                if (readCount == 0) R.color.text_dark_primary else R.color.text_dark_secondary))
        date.text = newsItem.published.toLocalDateTime()
        if (newsItem.enclosure != null) {
            GlideApp.with(image).load(newsItem.enclosure.url).centerCrop().into(image)
        } else {
            GlideApp.with(image).clear(image)
        }
    }

    override fun onClick(v: View) {
        if (newsItem != null) {
            val activity = v.context as Activity
            activity.startActivity(DetailActivity.newStartIntent(activity, newsItem!!),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity, image, "image").toBundle())

            title.setTextColor(ContextCompat.getColor(title.context, R.color.text_dark_secondary))
            listener.onNewsItemOpened(newsItem!!)
        }
    }
}

internal class NewsListAdapter(context: Context) : RecyclerView.Adapter<NewsItemViewHolder>(), NewsItemViewHolder.Listener {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val newsItems: ArrayList<Pair<NewsItem, Int>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        return NewsItemViewHolder(inflater, parent, this)
    }

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) {
        val item = newsItems[position]
        holder.bind(item.first, item.second)
    }

    override fun getItemCount(): Int = newsItems.size

    fun setNewsItem(newsItems: List<Pair<NewsItem, Int>>) {
        this.newsItems.clear()
        this.newsItems.addAll(newsItems)
        notifyDataSetChanged()
    }

    override fun onNewsItemOpened(newsItem: NewsItem) {
        for (i in 0 until newsItems.size) {
            val item = newsItems[i]
            if (item.first.uuid == newsItem.uuid) {
                newsItems[i] = Pair(item.first, item.second + 1)
                break
            }
        }
    }
}

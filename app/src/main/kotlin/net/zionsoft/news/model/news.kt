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

package net.zionsoft.news.model

import io.reactivex.Single
import retrofit2.Retrofit

data class NewsItem(val uuid: String, val title: String, val description: String?, val link: String,
                    val published: Long, val enclosure: Enclosure?) {
    data class Enclosure(val url: String, val mime: String)
}

class NewsModel(retrofit: Retrofit) {
    private val feedService: FeedService = retrofit.create(FeedService::class.java)

    fun loadLatestNews(): Single<List<NewsItem>> {
        return feedService.fetchRss("https://townhall.com/api/openaccess/news/").map { rss: Rss ->
            val rssItems = rss.channel?.items
            val count = rssItems?.size ?: 0
            val newsItems: ArrayList<NewsItem> = ArrayList(count)
            for (i in 0 until count) {
                val rssItem = rssItems!![i]
                val feedItem = rssItem.toFeedItem()
                if (feedItem != null) {
                    newsItems.add(feedItem)
                }
            }
            newsItems
        }
    }
}

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

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import io.reactivex.Single
import retrofit2.Retrofit

data class NewsItem(val uuid: String, val title: String, val description: String?, val link: String,
                    val published: Long, val enclosure: Enclosure?) {
    data class Enclosure(val url: String, val mime: String)
}

class NewsModel(private val databaseHelper: DatabaseHelper, retrofit: Retrofit) {
    private val feedService: FeedService = retrofit.create(FeedService::class.java)

    fun loadLatestNews(): Single<List<NewsItem>> {
        return feedService.fetchRss("https://townhall.com/api/openaccess/tipsheet/").map { rss: Rss ->
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
            newsItems as List<NewsItem>
        }.doOnSuccess { newsItems: List<NewsItem> ->
            NewsTableHelper.save(databaseHelper.getDatabase(), newsItems)
        }
    }
}

internal class NewsTableHelper {
    companion object {
        private const val TABLE_NAME: String = "news"
        private const val INDEX_PUBLISHED = "published"
        private const val COLUMN_UUID: String = "uuid"
        private const val COLUMN_TITLE: String = "title"
        private const val COLUMN_DESCRIPTION: String = "description"
        private const val COLUMN_LINK: String = "link"
        private const val COLUMN_IMAGE_URL: String = "image_url"
        private const val COLUMN_PUBLISHED: String = "published"

        fun createTable(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_UUID TEXT PRIMARY KEY," +
                    "$COLUMN_TITLE TEXT NOT NULL, " +
                    "$COLUMN_DESCRIPTION TEXT, " +
                    "$COLUMN_LINK TEXT NOT NULL, " +
                    "$COLUMN_IMAGE_URL TEXT," +
                    "$COLUMN_PUBLISHED INTEGER NOT NULL" +
                    ");")
            db.execSQL("CREATE INDEX $INDEX_PUBLISHED ON $TABLE_NAME ($COLUMN_PUBLISHED);")
        }

        fun save(db: SQLiteDatabase, newsItems: List<NewsItem>) {
            val contentValues = ContentValues(6)
            for (i in 0 until newsItems.size) {
                val newsItem = newsItems[i]
                contentValues.put(COLUMN_UUID, newsItem.uuid)
                contentValues.put(COLUMN_TITLE, newsItem.title)
                contentValues.put(COLUMN_DESCRIPTION, newsItem.description)
                contentValues.put(COLUMN_LINK, newsItem.link)
                if (newsItem.enclosure != null && newsItem.enclosure.mime.startsWith("image")) {
                    contentValues.put(COLUMN_IMAGE_URL, newsItem.enclosure.url)
                } else {
                    contentValues.remove(COLUMN_IMAGE_URL)
                }
                contentValues.put(COLUMN_PUBLISHED, newsItem.published)
                db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
            }
        }
    }
}

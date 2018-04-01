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
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Retrofit
import java.util.*

data class NewsItem(val uuid: String, val title: String, val description: String?, val link: String,
                    val published: Long, val enclosure: Enclosure?) : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uuid)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeString(link)
        dest.writeLong(published)
        dest.writeParcelable(enclosure, flags)
    }

    companion object CREATOR : Parcelable.Creator<NewsItem> {
        override fun createFromParcel(parcel: Parcel): NewsItem = NewsItem(parcel.readString(), parcel.readString(),
                parcel.readString(), parcel.readString(), parcel.readLong(), parcel.readParcelable(Enclosure::class.java.classLoader))

        override fun newArray(size: Int): Array<NewsItem?> = arrayOfNulls(size)
    }

    data class Enclosure(val url: String, val mime: String) : Parcelable {
        override fun describeContents(): Int = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(url)
            dest.writeString(mime)
        }

        companion object CREATOR : Parcelable.Creator<Enclosure> {
            override fun createFromParcel(parcel: Parcel): Enclosure = Enclosure(parcel.readString(), parcel.readString())

            override fun newArray(size: Int): Array<Enclosure?> = arrayOfNulls(size)
        }
    }
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
        }.onErrorReturn { Collections.emptyList() }.flatMapCompletable { Completable.complete() }
                .andThen(Single.fromCallable { NewsTableHelper.loadLatest(databaseHelper.getDatabase(), 50) })
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

        fun loadLatest(db: SQLiteDatabase, limit: Int): List<NewsItem> {
            var cursor: Cursor? = null
            try {
                cursor = db.query(TABLE_NAME, arrayOf(COLUMN_UUID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_LINK, COLUMN_IMAGE_URL, COLUMN_PUBLISHED),
                        null, null, null, null, "$COLUMN_PUBLISHED DESC", "$limit")

                val newsItems = ArrayList<NewsItem>(cursor.count)
                val uuidIndex = cursor.getColumnIndex(COLUMN_UUID)
                val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
                val descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)
                val linkIndex = cursor.getColumnIndex(COLUMN_LINK)
                val imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL)
                val publishedIndex = cursor.getColumnIndex(COLUMN_PUBLISHED)
                while (cursor.moveToNext()) {
                    val imageUrl = cursor.getString(imageUrlIndex)
                    val enclosure: NewsItem.Enclosure? = if (TextUtils.isEmpty(imageUrl)) null else NewsItem.Enclosure(imageUrl, "image/*")
                    newsItems.add(NewsItem(cursor.getString(uuidIndex), cursor.getString(titleIndex), cursor.getString(descriptionIndex),
                            cursor.getString(linkIndex), cursor.getLong(publishedIndex), enclosure))
                }
                return newsItems
            } finally {
                cursor?.close()
            }
        }
    }
}

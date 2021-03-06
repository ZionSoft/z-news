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
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*
import kotlin.collections.HashMap

class ReadHistoryModel(private val databaseHelper: DatabaseHelper) {
    fun loadReadCounts(uuids: List<String>): Single<Map<String, Int>> =
            Single.fromCallable { ReadHistoryTableHelper.loadReadCounts(databaseHelper.getDatabase(), uuids) }

    fun increaseReadCount(uuid: String): Completable =
            Completable.fromCallable {
                val db = databaseHelper.getDatabase()
                db.beginTransaction()
                try {
                    val currentReadCount = ReadHistoryTableHelper.loadReadCount(db, uuid)
                    ReadHistoryTableHelper.save(db, uuid, currentReadCount + 1)

                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }

                null
            }
}

internal class ReadHistoryTableHelper {
    companion object {
        private const val TABLE_NAME: String = "read_history"
        private const val COLUMN_UUID: String = "uuid"
        private const val COLUMN_READ_COUNT: String = "read_count"

        fun createTable(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_UUID TEXT PRIMARY KEY," +
                    "$COLUMN_READ_COUNT INTEGER NOT NULL" +
                    ");")
        }

        fun save(db: SQLiteDatabase, uuid: String, readCount: Int) {
            val contentValues = ContentValues(2)
            contentValues.put(COLUMN_UUID, uuid)
            contentValues.put(COLUMN_READ_COUNT, readCount)
            db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        }

        fun loadReadCount(db: SQLiteDatabase, uuid: String): Int {
            var cursor: Cursor? = null
            try {
                cursor = db.query(TABLE_NAME, arrayOf(COLUMN_READ_COUNT),
                        "$COLUMN_UUID = ?", arrayOf(uuid), null, null, null, null)
                return if (cursor.moveToFirst()) {
                    cursor.getInt(0)
                } else {
                    0
                }
            } finally {
                cursor?.close()
            }
        }

        fun loadReadCounts(db: SQLiteDatabase, uuids: List<String>): Map<String, Int> {
            if (uuids.isEmpty()) {
                return Collections.emptyMap()
            }

            val query = StringBuilder("SELECT $COLUMN_UUID, $COLUMN_READ_COUNT FROM $TABLE_NAME " +
                    "WHERE $COLUMN_UUID IN (")
            val selectionArgs = Array(uuids.size, { "" })
            for (i in 0 until uuids.size) {
                query.append("?, ")
                selectionArgs[i] = uuids[i]
            }
            query.delete(query.length - 2, query.length)
            query.append(");")

            var cursor: Cursor? = null
            try {
                cursor = db.rawQuery(query.toString(), selectionArgs)
                val readCounts = HashMap<String, Int>()
                val uuidIndex = cursor.getColumnIndex(COLUMN_UUID)
                val readCountIndex = cursor.getColumnIndex(COLUMN_READ_COUNT)
                while (cursor.moveToNext()) {
                    readCounts.put(cursor.getString(uuidIndex), cursor.getInt(readCountIndex))
                }
                return readCounts
            } finally {
                cursor?.close()
            }
        }
    }
}

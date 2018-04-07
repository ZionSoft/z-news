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

import android.database.sqlite.SQLiteDatabase

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
    }
}

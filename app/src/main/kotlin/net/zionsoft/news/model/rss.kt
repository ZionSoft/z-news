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

import android.text.TextUtils
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.text.SimpleDateFormat
import java.util.*

@Root(name = "enclosure", strict = false)
class RssEnclosure {
    @field:Attribute(name = "url")
    var url: String? = null

    @field:Attribute(name = "type")
    var mime: String? = null

    fun toEnclosure(): NewsItem.Enclosure? {
        return if (TextUtils.isEmpty(url) || TextUtils.isEmpty(mime)) {
            null
        } else {
            NewsItem.Enclosure(url!!, mime!!)
        }
    }
}

@Root(name = "item", strict = false)
class RssItem {
    companion object {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
    }

    @field:Element(name = "guid")
    var guid: String? = null

    @field:Element(name = "title")
    var title: String? = null

    @field:Element(name = "description")
    var description: String? = null

    @field:Element(name = "link")
    var link: String? = null

    @field:Element(name = "pubDate")
    var pubDate: String? = null

    @field:Element(name = "enclosure")
    var enclosure: RssEnclosure? = null

    fun toFeedItem(): NewsItem? {
        return if (TextUtils.isEmpty(guid) || TextUtils.isEmpty(title)
                || TextUtils.isEmpty(link) || TextUtils.isEmpty(pubDate)) {
            null
        } else {
            NewsItem(guid!!, title!!, description, link!!, dateFormat.parse(pubDate).time, enclosure?.toEnclosure())
        }
    }
}

@Root(name = "channel", strict = false)
class RssChannel {
    @field:Element(name = "title")
    var title: String? = null

    @field:ElementList(inline = true, name = "item")
    var items: List<RssItem>? = null
}

@Root(name = "rss", strict = false)
class Rss {
    @field:Element(name = "channel")
    var channel: RssChannel? = null
}

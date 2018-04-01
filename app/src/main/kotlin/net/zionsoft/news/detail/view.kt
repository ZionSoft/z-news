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

package net.zionsoft.news.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import net.zionsoft.news.R
import net.zionsoft.news.base.BaseActivity
import net.zionsoft.news.base.MVPView
import net.zionsoft.news.misc.GlideApp
import net.zionsoft.news.model.NewsItem
import net.zionsoft.news.utils.fadeIn

interface DetailView : MVPView

class DetailActivity : BaseActivity(), DetailView {
    companion object {
        private const val KEY_NEWS_ITEM = "net.zionsoft.news.KEY_NEWS_ITEM"

        fun newStartIntent(context: Context, newsItem: NewsItem): Intent {
            return Intent(context, DetailActivity::class.java).putExtra(KEY_NEWS_ITEM, newsItem)
        }
    }

    @BindView(R.id.title)
    internal lateinit var title: TextView

    @BindView(R.id.image)
    internal lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val newsItem: NewsItem = intent.getParcelableExtra(KEY_NEWS_ITEM)
        title.text = newsItem.title
        if (newsItem.enclosure != null) {
            GlideApp.with(image).load(newsItem.enclosure.url).centerCrop().into(image)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            title.alpha = 0.0F
            window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    title.fadeIn()
                }

                override fun onTransitionResume(transition: Transition) {}

                override fun onTransitionPause(transition: Transition) {}

                override fun onTransitionCancel(transition: Transition) {}

                override fun onTransitionStart(transition: Transition) {}
            })
        }
    }
}

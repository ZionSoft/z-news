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
import android.content.Intent
import android.os.Bundle
import net.zionsoft.news.R
import net.zionsoft.news.base.BaseActivity
import net.zionsoft.news.base.MVPView
import net.zionsoft.news.model.NewsItem
import javax.inject.Inject

interface HomeView : MVPView {
    fun onLatestNewsLoaded(newsItems: List<NewsItem>)
    fun onLatestNewsLoadFailed()
}

class HomeActivity : BaseActivity(), HomeView {
    companion object {
        fun newStartIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    @Inject
    internal lateinit var presenter: HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun onStart() {
        super.onStart()
        presenter.takeView(this)
        presenter.loadLatestNews()
    }

    override fun onStop() {
        presenter.dropView()
        super.onStop()
    }

    override fun onLatestNewsLoaded(newsItems: List<NewsItem>) {
        // TODO
    }

    override fun onLatestNewsLoadFailed() {
        // TODO
    }
}

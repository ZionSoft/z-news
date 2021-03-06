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
import android.os.SystemClock
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import butterknife.BindView
import net.zionsoft.news.R
import net.zionsoft.news.base.BaseActivity
import net.zionsoft.news.base.MVPView
import net.zionsoft.news.model.NewsItem
import javax.inject.Inject

interface HomeView : MVPView {
    fun onLatestNewsLoaded(newsItems: List<Pair<NewsItem, Int>>)
    fun onLatestNewsLoadFailed()
}

class HomeActivity : BaseActivity(), HomeView, SwipeRefreshLayout.OnRefreshListener {
    companion object {
        fun newStartIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    @BindView(R.id.swipe_container)
    lateinit var swipeContainer: SwipeRefreshLayout

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    @Inject
    internal lateinit var presenter: HomePresenter

    private lateinit var adapter: NewsListAdapter

    private var lastRefreshedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        swipeContainer.setColorSchemeResources(R.color.accent)
        swipeContainer.setOnRefreshListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NewsListAdapter(this)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        presenter.takeView(this)
        loadLatestNews(false)
    }

    private fun loadLatestNews(forceRefresh: Boolean) {
        if (!forceRefresh
                && adapter.itemCount > 0
                && SystemClock.elapsedRealtime() - lastRefreshedTime <= 5L * DateUtils.MINUTE_IN_MILLIS) {
            return
        }

        swipeContainer.isRefreshing = true
        presenter.loadLatestNews()
    }

    override fun onStop() {
        presenter.dropView()
        super.onStop()
    }

    override fun onLatestNewsLoaded(newsItems: List<Pair<NewsItem, Int>>) {
        swipeContainer.isRefreshing = false
        adapter.setNewsItem(newsItems)
        lastRefreshedTime = SystemClock.elapsedRealtime()
    }

    override fun onLatestNewsLoadFailed() {
        // TODO
    }

    override fun onRefresh() {
        loadLatestNews(true)
    }
}

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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
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

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.loading_spinner)
    lateinit var loadingSpinner: ProgressBar

    @Inject
    internal lateinit var presenter: HomePresenter

    private lateinit var adapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NewsListAdapter(this)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        presenter.takeView(this)
        loadLatestNews()
    }

    private fun loadLatestNews() {
        loadingSpinner.visibility = View.VISIBLE
        presenter.loadLatestNews()
    }

    override fun onStop() {
        presenter.dropView()
        super.onStop()
    }

    override fun onLatestNewsLoaded(newsItems: List<NewsItem>) {
        loadingSpinner.visibility = View.GONE
        adapter.setNewsItem(newsItems)
    }

    override fun onLatestNewsLoadFailed() {
        // TODO
    }
}

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

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import net.zionsoft.news.base.MVPPresenter
import net.zionsoft.news.model.NewsItem
import net.zionsoft.news.model.NewsModel
import timber.log.Timber

class HomePresenter(private val newsModel: NewsModel) : MVPPresenter<HomeView>() {
    private var loadLatestNewsDisposable: Disposable? = null

    override fun onViewDropped() {
        disposeLoadLatestNews()
        super.onViewDropped()
    }

    private fun disposeLoadLatestNews() {
        loadLatestNewsDisposable?.dispose()
        loadLatestNewsDisposable = null
    }

    internal fun loadLatestNews() {
        disposeLoadLatestNews()
        loadLatestNewsDisposable = newsModel.loadLatestNews()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<NewsItem>>() {
                    override fun onSuccess(newsItems: List<NewsItem>) {
                        Timber.d("Latest news loaded")
                        loadLatestNewsDisposable = null
                        view?.onLatestNewsLoaded(newsItems)
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e, "Failed to load latest news")
                        loadLatestNewsDisposable = null
                        view?.onLatestNewsLoadFailed()
                    }
                })
    }
}

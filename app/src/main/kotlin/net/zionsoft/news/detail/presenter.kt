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

import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import net.zionsoft.news.base.MVPPresenter
import net.zionsoft.news.model.ReadHistoryModel
import timber.log.Timber

class DetailPresenter(private val readHistoryModel: ReadHistoryModel) : MVPPresenter<DetailView>() {
    fun increaseReadCount(uuid: String) {
        readHistoryModel.increaseReadCount(uuid)
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        Timber.d("Read count increased")
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e, "Failed to increase read count")
                    }
                })
    }
}

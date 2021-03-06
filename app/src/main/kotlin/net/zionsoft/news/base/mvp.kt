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

package net.zionsoft.news.base

import android.support.annotation.CallSuper

interface MVPView

open class MVPPresenter<V : MVPView> {
    protected var view: V? = null
        private set

    fun takeView(view: V) {
        this.view = view
        onViewTaken()
    }

    @CallSuper
    protected open fun onViewTaken() {
    }

    fun dropView() {
        onViewDropped()
        view = null
    }

    @CallSuper
    protected open fun onViewDropped() {
    }
}

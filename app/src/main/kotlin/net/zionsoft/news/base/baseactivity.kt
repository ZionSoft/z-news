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

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import dagger.android.AndroidInjection

abstract class BaseActivity : AppCompatActivity() {
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun setContentView(view: View) {
        super.setContentView(view)
        ButterKnife.bind(this)
    }

    @CallSuper
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        ButterKnife.bind(this)
    }

    @CallSuper
    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(view, params)
        ButterKnife.bind(this)
    }
}

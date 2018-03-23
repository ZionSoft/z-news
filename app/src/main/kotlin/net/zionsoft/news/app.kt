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

package net.zionsoft.news

import android.app.Activity
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import net.zionsoft.news.utils.initDateTimeUtils
import net.zionsoft.news.utils.initTextFormatter
import javax.inject.Inject

class App : BaseApp(), HasActivityInjector {
    companion object {
        lateinit var appComponent: AppComponent
            private set
    }

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
        initDateTimeUtils(this)
        initTextFormatter(this)

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        appComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }
}

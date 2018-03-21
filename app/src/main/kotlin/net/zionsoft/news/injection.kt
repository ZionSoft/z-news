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
import android.content.Context
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.ActivityKey
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import net.zionsoft.news.home.HomeActivity
import net.zionsoft.news.home.HomeSubcomponent
import javax.inject.Singleton

@Module
open class BaseAppModule(private val app: App) {
    @Provides
    @Singleton
    fun provideAppContext(): Context {
        return app
    }
}

@Module(subcomponents = [(HomeSubcomponent::class)])
abstract class ActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(HomeActivity::class)
    internal abstract fun bindHomeActivityInjectorFactory(builder: HomeSubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Singleton
@Component(modules = [(AppModule::class), (AndroidInjectionModule::class), (ActivityModule::class)])
interface AppComponent {
    fun inject(app: App)
}

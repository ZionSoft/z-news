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

import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.zionsoft.news.model.NewsModel
import net.zionsoft.news.model.ReadHistoryModel

@Module
class HomeModule {
    @Provides
    fun provideHomePresenter(newsModel: NewsModel, readHistoryModel: ReadHistoryModel): HomePresenter =
            HomePresenter(newsModel, readHistoryModel)
}

@Subcomponent(modules = [(HomeModule::class)])
interface HomeSubcomponent : AndroidInjector<HomeActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<HomeActivity>()
}

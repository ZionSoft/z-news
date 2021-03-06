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

import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.zionsoft.news.model.ReadHistoryModel

@Module
class DetailModule {
    @Provides
    fun provideDetailPresenter(readHistoryModel: ReadHistoryModel): DetailPresenter = DetailPresenter(readHistoryModel)
}

@Subcomponent(modules = [(DetailModule::class)])
interface DetailSubcomponent : AndroidInjector<DetailActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DetailActivity>()
}

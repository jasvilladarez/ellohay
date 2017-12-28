/*
 * MIT License
 *
 * Copyright (c) 2017 Jasmine Villadarez
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.jasvilladarez.ello.browse.discover

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.jasvilladarez.domain.entity.Category
import io.github.jasvilladarez.domain.repository.browse.BrowseRepository
import io.github.jasvilladarez.ello.common.MviStateMachine
import io.github.jasvilladarez.ello.common.MviViewModel
import io.github.jasvilladarez.ello.util.applyMvi
import io.reactivex.Observable

internal class DiscoverViewModel(
        private val browseRepository: BrowseRepository
) : ViewModel(), MviViewModel<DiscoverIntent, DiscoverViewState> {

    private val stateMachine =
            MviStateMachine<DiscoverIntent, DiscoverResult, DiscoverViewState>(
                    DiscoverViewState.DefaultCategoryView(), {
                when (it) {
                    is DiscoverIntent.LoadCategories -> fetchCategories()
                    is DiscoverIntent.LoadPosts -> fetchPosts(it.selectedItem)
                    is DiscoverIntent.LoadMorePosts -> fetchPosts(it.selectedItem, it.nextPageId)
                }
            }, { _, result ->
                when (result) {
                    is DiscoverResult.SuccessCategories ->
                        DiscoverViewState.DefaultCategoryView(result.categories)
                    is DiscoverResult.SuccessPosts -> when (result.mode) {
                        DiscoverResult.DiscoverMode.LOAD_POSTS ->
                            DiscoverViewState.DefaultPostsView(result.postStream.posts,
                                    result.postStream.next)
                        DiscoverResult.DiscoverMode.LOAD_MORE_POSTS ->
                            DiscoverViewState.MorePostsView(result.postStream.posts,
                                    result.postStream.next)

                    }
                    is DiscoverResult.Error -> DiscoverViewState.ErrorView(result.error.message)
                    is DiscoverResult.InProgressCategories ->
                        DiscoverViewState.LoadingCategoriesView
                    is DiscoverResult.InProgressPosts -> when (result.mode) {
                        DiscoverResult.DiscoverMode.LOAD_POSTS ->
                            DiscoverViewState.LoadingPostsView
                        DiscoverResult.DiscoverMode.LOAD_MORE_POSTS ->
                            DiscoverViewState.LoadingMorePostsView
                    }
                }
            })

    override val state: LiveData<DiscoverViewState>
        get() = stateMachine.state

    override fun processIntents(intents: Observable<DiscoverIntent>) {
        stateMachine.processIntents(intents)
    }

    private fun fetchCategories(): Observable<DiscoverResult> = browseRepository
            .fetchCategories().applyMvi(
            { DiscoverResult.SuccessCategories(it) },
            { DiscoverResult.Error(it) },
            { DiscoverResult.InProgressCategories })

    private fun fetchPosts(category: Category,
                           nextPageId: String? = null): Observable<DiscoverResult> =
            browseRepository.fetchPostsByCategory(category, nextPageId).applyMvi(
                    {
                        DiscoverResult.SuccessPosts(it, nextPageId?.let {
                            DiscoverResult.DiscoverMode.LOAD_MORE_POSTS
                        } ?: DiscoverResult.DiscoverMode.LOAD_POSTS)
                    },
                    { DiscoverResult.Error(it) },
                    {
                        nextPageId?.let {
                            DiscoverResult.InProgressPosts(DiscoverResult.DiscoverMode.LOAD_MORE_POSTS)
                        } ?: DiscoverResult.InProgressPosts(DiscoverResult.DiscoverMode.LOAD_POSTS)
                    }
            )
}
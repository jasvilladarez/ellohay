/*
 * MIT License
 *
 * Copyright (c) 2018 Jasmine Villadarez
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

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.jasvilladarez.ello.InstantTaskSpekRule
import io.github.jasvilladarez.ello.domain.repository.browse.BrowseRepository
import io.github.jasvilladarez.ello.domain.util.toObservable
import io.github.jasvilladarez.ello.domain.util.toObservableError
import io.github.jasvilladarez.test.common.RxSpekRule
import io.github.jasvilladarez.test.common.SpekRule
import io.github.jasvilladarez.test.common.addEachTestRule
import io.github.jasvilladarez.test.common.addGroupRules
import io.github.jasvilladarez.test.objects.BrowseRepositoryTestObject
import org.amshove.kluent.mock
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

internal object DiscoverViewModelTest : Spek({
    addGroupRules(RxSpekRule(), InstantTaskSpekRule())
    val browseRepository by memoized { mock<BrowseRepository>() }

    given("a DiscoverViewModel") {
        val discoverViewModel by memoized { DiscoverViewModel(browseRepository) }
        val observer by memoized { mock<Observer<DiscoverViewState>>() }
        addEachTestRule(SpekRule({ discoverViewModel.state.observeForever(observer) },
                { discoverViewModel.state.removeObserver(observer) }))
        context("DiscoverIntent.LoadCategories") {
            on("success") {
                whenever(browseRepository.fetchCategories())
                        .thenReturn(BrowseRepositoryTestObject.categories.toObservable())
                discoverViewModel.processIntents(DiscoverIntent.LoadCategories.toObservable())

                it("should return categories view state") {
                    verify(observer).onChanged(DiscoverViewState.DefaultCategoryView())
                    verify(observer).onChanged(DiscoverViewState.LoadingCategoriesView)
                    verify(observer).onChanged(DiscoverViewState.DefaultCategoryView(
                            BrowseRepositoryTestObject.categories.map { it.mapToViewItem() }
                    ))
                }
            }
            on("error") {
                val errorMessage by memoized { "There's an error" }
                whenever(browseRepository.fetchCategories())
                        .thenReturn(RuntimeException(errorMessage).toObservableError())
                discoverViewModel.processIntents(DiscoverIntent.LoadCategories.toObservable())

                it("should return error view state") {
                    verify(observer).onChanged(DiscoverViewState.DefaultCategoryView())
                    verify(observer).onChanged(DiscoverViewState.LoadingCategoriesView)
                    verify(observer).onChanged(DiscoverViewState.ErrorView(errorMessage))
                }
            }
        }

        context("DiscoverIntent.LoadPosts") {
            val slug by memoized { "slug" }
            on("success") {
                whenever(browseRepository.fetchPostsByCategory(slug))
                        .thenReturn(BrowseRepositoryTestObject.postStream.toObservable())
                discoverViewModel.processIntents(DiscoverIntent.LoadPosts(
                        CategoryItem(slug, slug, "")).toObservable())

                it("should return load post view state") {
                    verify(observer).onChanged(DiscoverViewState.DefaultCategoryView())
                    verify(observer).onChanged(DiscoverViewState.LoadingPostsView)
                    verify(observer).onChanged(DiscoverViewState.DefaultPostsView(
                            BrowseRepositoryTestObject.postStream.mapToViewItems(),
                            BrowseRepositoryTestObject.postStream.next
                    ))
                }
            }
            on("error") {
                val errorMessage by memoized { "error" }
                whenever(browseRepository.fetchPostsByCategory(slug))
                        .thenReturn(RuntimeException(errorMessage).toObservableError())
                discoverViewModel.processIntents(DiscoverIntent.LoadPosts(
                        CategoryItem(slug, slug, "")).toObservable())

                it("should return error view state") {
                    verify(observer).onChanged(DiscoverViewState.DefaultCategoryView())
                    verify(observer).onChanged(DiscoverViewState.LoadingPostsView)
                    verify(observer).onChanged(DiscoverViewState.ErrorView(errorMessage))
                }
            }
        }

        context("DiscoverIntent.LoadMorePosts") {
            val slug by memoized { "slug" }
            val nextPageId by memoized { "123" }
            on("success") {
                whenever(browseRepository.fetchPostsByCategory(slug, nextPageId))
                        .thenReturn(BrowseRepositoryTestObject.postStream.toObservable())
                discoverViewModel.processIntents(DiscoverIntent.LoadMorePosts(
                        CategoryItem(slug, slug, ""), nextPageId).toObservable())

                it("should return load more post view state") {
                    verify(observer).onChanged(DiscoverViewState.DefaultCategoryView())
                    verify(observer).onChanged(DiscoverViewState.LoadingMorePostsView)
                    verify(observer).onChanged(DiscoverViewState.MorePostsView(
                            BrowseRepositoryTestObject.postStream.mapToViewItems(),
                            BrowseRepositoryTestObject.postStream.next
                    ))
                }
            }
            on("error") {
                val errorMessage by memoized { "error" }
                whenever(browseRepository.fetchPostsByCategory(slug, nextPageId))
                        .thenReturn(RuntimeException(errorMessage).toObservableError())
                discoverViewModel.processIntents(DiscoverIntent.LoadMorePosts(
                        CategoryItem(slug, slug, ""), nextPageId).toObservable())

                it("should return error view state") {
                    verify(observer).onChanged(DiscoverViewState.DefaultCategoryView())
                    verify(observer).onChanged(DiscoverViewState.LoadingMorePostsView)
                    verify(observer).onChanged(DiscoverViewState.ErrorView(errorMessage))
                }
            }
        }
    }
})
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

package io.github.jasvilladarez.ello.browse.editorial

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.jasvilladarez.domain.repository.browse.BrowseRepository
import io.github.jasvilladarez.domain.util.toObservable
import io.github.jasvilladarez.domain.util.toObservableError
import io.github.jasvilladarez.ello.addInstantTaskRule
import io.github.jasvilladarez.ello.addRxScheduling
import io.github.jasvilladarez.ello.browse.BrowseRepositoryTestObject
import org.amshove.kluent.mock
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

internal object EditorialViewModelTest : Spek({
    addInstantTaskRule()
    addRxScheduling()
    val browseRepository by memoized { mock<BrowseRepository>() }

    given("a EditorialViewModel") {
        val editorialViewModel by memoized { EditorialViewModel(browseRepository) }
        val observer by memoized { mock<Observer<EditorialViewState>>() }
        beforeEachTest { editorialViewModel.state.observeForever(observer) }
        afterEachTest { editorialViewModel.state.removeObserver(observer) }
        context("EditorialIntent.Load ") {
            on("success") {
                whenever(browseRepository.fetchEditorials())
                        .thenReturn(BrowseRepositoryTestObject.editorialStream.toObservable())
                editorialViewModel.processIntents(EditorialIntent.Load.toObservable())

                it("should emit default view state") {
                    verify(observer).onChanged(EditorialViewState.DefaultView())
                    verify(observer).onChanged(EditorialViewState.InitialLoadingView)
                    verify(observer).onChanged(EditorialViewState.DefaultView(
                            BrowseRepositoryTestObject.editorialStream.editorials
                                    .map { it.mapToViewItem() },
                            BrowseRepositoryTestObject.editorialStream.next))
                }
            }
            on("error") {
                val errorMessage by memoized { "There's an error" }
                whenever(browseRepository.fetchEditorials())
                        .thenReturn(RuntimeException(errorMessage).toObservableError())
                editorialViewModel.processIntents(EditorialIntent.Load.toObservable())

                it("should emit error view state") {
                    verify(observer).onChanged(EditorialViewState.DefaultView())
                    verify(observer).onChanged(EditorialViewState.InitialLoadingView)
                    verify(observer).onChanged(EditorialViewState.ErrorView(errorMessage))
                }
            }
        }

        context("EditorialIntent.LoadMore") {
            val nextPageId by memoized { "123" }
            on("success") {
                whenever(browseRepository.fetchEditorials(nextPageId))
                        .thenReturn(BrowseRepositoryTestObject.editorialStream.toObservable())
                editorialViewModel.processIntents(EditorialIntent.LoadMore(nextPageId).toObservable())

                it("should emit load more view state") {
                    verify(observer).onChanged(EditorialViewState.DefaultView())
                    verify(observer).onChanged(EditorialViewState.MoreLoadingView)
                    verify(observer).onChanged(EditorialViewState.MoreView(
                            BrowseRepositoryTestObject.editorialStream.editorials
                                    .map { it.mapToViewItem() },
                            BrowseRepositoryTestObject.editorialStream.next))
                }
            }

            on("error") {
                val errorMsg = "There's an error"
                whenever(browseRepository.fetchEditorials(nextPageId))
                        .thenReturn(RuntimeException(errorMsg).toObservableError())
                editorialViewModel.processIntents(EditorialIntent.LoadMore(nextPageId).toObservable())

                it("should emit error view state") {
                    verify(observer).onChanged(EditorialViewState.DefaultView())
                    verify(observer).onChanged(EditorialViewState.MoreLoadingView)
                    verify(observer).onChanged(EditorialViewState.ErrorView(errorMsg))
                }
            }
        }
    }
})
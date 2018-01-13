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

package io.github.jasvilladarez.ello.browse.invites

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.jasvilladarez.domain.repository.browse.BrowseRepository
import io.github.jasvilladarez.domain.util.toObservable
import io.github.jasvilladarez.domain.util.toObservableError
import io.github.jasvilladarez.ello.InstantTaskSpekRule
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

internal object ArtistInvitesViewModelTest : Spek({
    addGroupRules(RxSpekRule(), InstantTaskSpekRule())
    val browseRepository by memoized { mock<BrowseRepository>() }

    given("a ArtistInvitesViewModel") {
        val artistInvitesViewModel by memoized { ArtistInvitesViewModel(browseRepository) }
        val observer by memoized { mock<Observer<ArtistInvitesViewState>>() }
        addEachTestRule(SpekRule({ artistInvitesViewModel.state.observeForever(observer) },
                { artistInvitesViewModel.state.removeObserver(observer) }))
        context("ArtistInvitesIntent.Load") {
            on("success") {
                whenever(browseRepository.fetchArtistInvites(null))
                        .thenReturn(BrowseRepositoryTestObject.artistInviteStream.toObservable())
                artistInvitesViewModel.processIntents(ArtistInvitesIntent.Load.toObservable())

                it("should return default view state") {
                    verify(observer).onChanged(ArtistInvitesViewState.DefaultView())
                    verify(observer).onChanged(ArtistInvitesViewState.InitialLoadingView)
                    verify(observer).onChanged(ArtistInvitesViewState.DefaultView(
                            BrowseRepositoryTestObject.artistInviteStream.artistInvites
                                    .map { it.mapToViewItem() },
                            BrowseRepositoryTestObject.artistInviteStream.next
                    ))
                }
            }
            on("error") {
                val errorMessage by memoized { "There's an error" }
                whenever(browseRepository.fetchArtistInvites())
                        .thenReturn(RuntimeException(errorMessage).toObservableError())
                artistInvitesViewModel.processIntents(ArtistInvitesIntent.Load.toObservable())

                it("should return error view state") {
                    verify(observer).onChanged(ArtistInvitesViewState.DefaultView())
                    verify(observer).onChanged(ArtistInvitesViewState.InitialLoadingView)
                    verify(observer).onChanged(ArtistInvitesViewState.ErrorView(errorMessage))
                }
            }
        }

        context("ArtistInvitesIntent.LoadMore") {
            val nextPageId by memoized { "123" }
            on("success") {
                whenever(browseRepository.fetchArtistInvites(nextPageId))
                        .thenReturn(BrowseRepositoryTestObject.artistInviteStream.toObservable())
                artistInvitesViewModel.processIntents(
                        ArtistInvitesIntent.LoadMore(nextPageId).toObservable())

                it("should emit load more view state") {
                    verify(observer).onChanged(ArtistInvitesViewState.DefaultView())
                    verify(observer).onChanged(ArtistInvitesViewState.MoreLoadingView)
                    verify(observer).onChanged(ArtistInvitesViewState.MoreView(
                            BrowseRepositoryTestObject.artistInviteStream.artistInvites
                                    .map { it.mapToViewItem() },
                            BrowseRepositoryTestObject.artistInviteStream.next))
                }
            }

            on("error") {
                val errorMsg = "There's an error"
                whenever(browseRepository.fetchArtistInvites(nextPageId))
                        .thenReturn(RuntimeException(errorMsg).toObservableError())
                artistInvitesViewModel.processIntents(
                        ArtistInvitesIntent.LoadMore(nextPageId).toObservable())

                it("should emit error view state") {
                    verify(observer).onChanged(ArtistInvitesViewState.DefaultView())
                    verify(observer).onChanged(ArtistInvitesViewState.MoreLoadingView)
                    verify(observer).onChanged(ArtistInvitesViewState.ErrorView(errorMsg))
                }
            }
        }
    }
})
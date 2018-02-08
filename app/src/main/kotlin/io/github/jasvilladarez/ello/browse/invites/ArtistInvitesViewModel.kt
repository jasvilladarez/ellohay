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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.jasvilladarez.ello.common.MviStateMachine
import io.github.jasvilladarez.ello.common.MviViewModel
import io.github.jasvilladarez.ello.domain.repository.browse.BrowseRepository
import io.github.jasvilladarez.ello.util.applyMvi
import io.reactivex.Observable

internal class ArtistInvitesViewModel(
        private val browseRepository: BrowseRepository
) : ViewModel(), MviViewModel<ArtistInvitesIntent,
        ArtistInvitesViewState> {

    private val stateMachine =
            MviStateMachine<ArtistInvitesIntent, ArtistInvitesResult, ArtistInvitesViewState>(
                    ArtistInvitesViewState.DefaultView(), {
                when (it) {
                    is ArtistInvitesIntent.Load -> fetchArtistInvites()
                    is ArtistInvitesIntent.LoadMore -> fetchArtistInvites(it.nextPageId)
                }
            }, { _, result ->
                when (result) {
                    is ArtistInvitesResult.Success -> when (result.mode) {
                        ArtistInvitesResult.ArtistInviteMode.LOAD ->
                            ArtistInvitesViewState.DefaultView(result.artistInvites,
                                    result.nextPageId)
                        ArtistInvitesResult.ArtistInviteMode.LOAD_MORE ->
                            ArtistInvitesViewState.MoreView(result.artistInvites,
                                    result.nextPageId)
                    }
                    is ArtistInvitesResult.Error ->
                        ArtistInvitesViewState.ErrorView(result.error.message)
                    is ArtistInvitesResult.InProgress -> when (result.mode) {
                        ArtistInvitesResult.ArtistInviteMode.LOAD ->
                            ArtistInvitesViewState.InitialLoadingView
                        ArtistInvitesResult.ArtistInviteMode.LOAD_MORE ->
                            ArtistInvitesViewState.MoreLoadingView
                    }
                }
            })

    override val state: LiveData<ArtistInvitesViewState>
        get() = stateMachine.state

    override fun processIntents(intents: Observable<ArtistInvitesIntent>) {
        stateMachine.processIntents(intents)
    }

    override fun onCleared() {
        super.onCleared()
        stateMachine.clear()
    }

    private fun fetchArtistInvites(nextPageId: String? = null): Observable<ArtistInvitesResult> =
            browseRepository.fetchArtistInvites(nextPageId).applyMvi(
                    {
                        ArtistInvitesResult.Success(it.artistInvites.map { it.mapToViewItem() },
                                it.next, nextPageId?.let {
                            ArtistInvitesResult.ArtistInviteMode.LOAD_MORE
                        } ?: ArtistInvitesResult.ArtistInviteMode.LOAD)
                    },
                    { ArtistInvitesResult.Error(it) },
                    {
                        ArtistInvitesResult.InProgress(nextPageId?.let {
                            ArtistInvitesResult.ArtistInviteMode.LOAD_MORE
                        } ?: ArtistInvitesResult.ArtistInviteMode.LOAD)
                    })

}
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

package io.github.jasvilladarez.ello.browse.invites

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.jasvilladarez.ello.common.MviStateMachine
import io.github.jasvilladarez.ello.common.MviViewModel
import io.reactivex.Observable

internal class ArtistInvitesViewModel : ViewModel(), MviViewModel<ArtistInvitesIntent,
        ArtistInvitesViewState> {

    private val stateMachine =
            MviStateMachine<ArtistInvitesIntent, ArtistInvitesResult, ArtistInvitesViewState>(
                    ArtistInvitesViewState.DefaultView, {
                when (it) {
                    is ArtistInvitesIntent.Load -> Observable.just(ArtistInvitesResult.Success)
                }
            }, { _, result ->
                when (result) {
                    is ArtistInvitesResult.Success -> ArtistInvitesViewState.DefaultView
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
}
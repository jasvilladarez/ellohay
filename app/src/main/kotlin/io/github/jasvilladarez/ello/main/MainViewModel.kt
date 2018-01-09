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

package io.github.jasvilladarez.ello.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.jasvilladarez.domain.repository.auth.AuthRepository
import io.github.jasvilladarez.ello.common.MviStateMachine
import io.github.jasvilladarez.ello.common.MviViewModel
import io.github.jasvilladarez.ello.util.applyMvi
import io.reactivex.Observable

internal class MainViewModel(
        private val authRepository: AuthRepository
) : ViewModel(), MviViewModel<MainIntent, MainViewState> {

    private val stateMachine =
            MviStateMachine<MainIntent, MainResult, MainViewState>(MainViewState.View(), {
                when (it) {
                    is MainIntent.Load -> fetchAccessToken(it.currentTime)
                }
            }, { _, result ->
                when (result) {
                    is MainResult.Success -> MainViewState.View(true)
                    is MainResult.Error -> MainViewState.Error(result.error.message)
                    is MainResult.InProgress -> MainViewState.LoadingView
                }
            })

    override fun processIntents(intents: Observable<MainIntent>) {
        stateMachine.processIntents(intents)
    }

    override val state: LiveData<MainViewState>
        get() = stateMachine.state

    override fun onCleared() {
        super.onCleared()
        stateMachine.clear()
    }

    private fun fetchAccessToken(currentTime: Long): Observable<MainResult> =
            authRepository.fetchAccessToken(currentTime).applyMvi({ MainResult.Success },
                    { MainResult.Error(it) }, { MainResult.InProgress })
}
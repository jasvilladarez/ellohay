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

package io.github.jasvilladarez.ello.main

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.jasvilladarez.domain.entity.Token
import io.github.jasvilladarez.domain.repository.auth.AuthRepository
import io.github.jasvilladarez.domain.util.toObservable
import io.github.jasvilladarez.domain.util.toObservableError
import io.github.jasvilladarez.ello.InstantTaskSpekRule
import io.github.jasvilladarez.test.common.RxSpekRule
import io.github.jasvilladarez.test.common.addGroupRules
import org.amshove.kluent.mock
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

internal object MainViewModelTest : Spek({
    addGroupRules(RxSpekRule(), InstantTaskSpekRule())
    val authRepository by memoized { mock(AuthRepository::class) }

    given("a MainViewModel") {
        val mainViewModel by memoized { MainViewModel(authRepository) }
        val observer by memoized { mock<Observer<MainViewState>>() }
        beforeEachTest { mainViewModel.state.observeForever(observer) }
        afterEachTest { mainViewModel.state.removeObserver(observer) }
        context("fetchAccessToken") {
            on("success") {
                val currentTime by memoized { System.currentTimeMillis() }
                whenever(authRepository.fetchAccessToken(currentTime))
                        .thenReturn(Token.default().toObservable())
                mainViewModel.processIntents(MainIntent.Load(currentTime).toObservable())

                it("should emit success view state") {
                    verify(observer).onChanged(MainViewState.View())
                    verify(observer).onChanged(MainViewState.LoadingView)
                    verify(observer).onChanged(MainViewState.View(true))
                }
            }

            on("error") {
                val currentTime by memoized { System.currentTimeMillis() }
                val errorMessage by memoized { "There's an error" }
                whenever(authRepository.fetchAccessToken(currentTime))
                        .thenReturn(RuntimeException(errorMessage).toObservableError())
                mainViewModel.processIntents(MainIntent.Load(currentTime).toObservable())

                it("should emit error view state") {
                    verify(observer).onChanged(MainViewState.View())
                    verify(observer).onChanged(MainViewState.LoadingView)
                    verify(observer).onChanged(MainViewState.Error(errorMessage))
                }
            }
        }
    }
})
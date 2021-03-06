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

import io.github.jasvilladarez.ello.common.MviIntent
import io.github.jasvilladarez.ello.common.MviResult
import io.github.jasvilladarez.ello.common.MviViewState

internal sealed class MainIntent : MviIntent {

    data class Load(
            val currentTime: Long = System.currentTimeMillis()
    ) : MainIntent()
}

internal sealed class MainResult : MviResult {

    object Success : MainResult()

    data class Error(
            val error: Throwable
    ) : MainResult()

    object InProgress : MainResult()
}

internal sealed class MainViewState : MviViewState {

    data class View(
            val isSuccessful: Boolean = false,
            // TODO handle logged in view (Phase 2 implementation)
            val isLoggedIn: Boolean = false
    ) : MainViewState()

    object LoadingView : MainViewState()

    data class Error(
            val errorMessage: String?
    ) : MainViewState()
}
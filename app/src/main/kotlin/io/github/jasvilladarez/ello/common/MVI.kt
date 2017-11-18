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

package io.github.jasvilladarez.ello.common

import android.arch.lifecycle.LiveData
import io.reactivex.Observable

/**
 * Represents the intention/s of the user
 * @see MviView.intents
 */
internal interface MviIntent

/**
 * Represents the result after the action was taken
 */
internal interface MviResult

/**
 * Represents the state which the view should render
 * @see MviView.render
 */
internal interface MviViewState

/**
 * Represents the view to which the users sends the intent
 * and where the result is being rendered to
 */
internal interface MviView<I : MviIntent, in S : MviViewState> {

    fun intents(): Observable<I>

    fun render(state: S)
}

/**
 * Represents the object to which the view will subscribe to
 */
internal interface MviViewModel<I : MviIntent, S : MviViewState> {

    fun processIntents(intents: Observable<I>)

    val state: LiveData<S>
}
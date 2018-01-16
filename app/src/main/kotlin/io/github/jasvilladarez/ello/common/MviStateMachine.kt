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

package io.github.jasvilladarez.ello.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

internal class MviStateMachine<in I : MviIntent, R : MviResult, S : MviViewState>(
        initialState: S,
        resultFromIntent: (I) -> Observable<R>,
        reducer: (prevState: S, result: R) -> S
) {

    val state: LiveData<S> get() = mutableState

    private val mutableState: MutableLiveData<S> = MutableLiveData()

    private val disposables = CompositeDisposable()

    private val intents = BehaviorRelay.create<I>()

    init {
        intents.flatMap(resultFromIntent)
                .scan(initialState, reducer)
                .subscribe { mutableState.postValue(it) }
                .disposeOnCleared()
    }

    fun processIntents(intent: Observable<out I>) {
        intent.subscribe(intents).disposeOnCleared()
    }

    fun clear() {
        disposables.clear()
    }

    private fun Disposable.disposeOnCleared() {
        disposables.add(this)
    }

}
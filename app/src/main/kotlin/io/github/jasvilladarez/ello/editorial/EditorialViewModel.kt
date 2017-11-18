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

package io.github.jasvilladarez.ello.editorial

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.github.jasvilladarez.ello.common.MviViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

internal class EditorialViewModel : ViewModel(), MviViewModel<EditorialIntent, EditorialViewState> {

    private val intentSubject: PublishSubject<EditorialIntent> =
            PublishSubject.create<EditorialIntent>()

    override val state: LiveData<EditorialViewState>
        get() = _state

    private val _state: MutableLiveData<EditorialViewState> = MutableLiveData()

    override fun processIntents(intents: Observable<EditorialIntent>) {
        intents.subscribe(intentSubject)
        intentSubject.flatMap {
            when (it) {
                is EditorialIntent.InitialIntent ->
                    Observable.just(EditorialViewState.View(false) as EditorialViewState)
                            .delay(10, TimeUnit.SECONDS)
                is EditorialIntent.ButtonClickedIntent ->
                    Observable.just(EditorialViewState.View(false,
                            it.buttonText) as EditorialViewState)
            }
        }.startWith(EditorialViewState.View(true) as EditorialViewState).subscribe({
            _state.postValue(it)
            Log.d("Test", "test")
        }, {
            Log.d("Test", "error")
        })
    }

}
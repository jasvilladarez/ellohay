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

package io.github.jasvilladarez.ello.discover.editorial

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.jasvilladarez.domain.interactor.editorial.EditorialInteractor
import io.github.jasvilladarez.ello.common.MviStateMachine
import io.github.jasvilladarez.ello.common.MviViewModel
import io.github.jasvilladarez.ello.util.applyMvi
import io.reactivex.Observable

internal class EditorialViewModel(
        private val editorialInteractor: EditorialInteractor
) : ViewModel(), MviViewModel<EditorialIntent, EditorialViewState> {

    private val stateMachine =
            MviStateMachine<EditorialIntent, EditorialResult, EditorialViewState>(EditorialViewState.View(), {
                when (it) {
                    is EditorialIntent.Load -> fetchEditorials()
                }
            }, { _, result ->
                when (result) {
                    is EditorialResult.Success -> {
                        EditorialViewState.View(result.editorials)
                    }
                    is EditorialResult.Error -> {
                        EditorialViewState.Error(result.error.message)
                    }
                    is EditorialResult.InProgress -> EditorialViewState.View(isLoading = true)
                }
            })


    override val state: LiveData<EditorialViewState>
        get() = stateMachine.state


    override fun processIntents(intents: Observable<EditorialIntent>) {
        stateMachine.processIntents(intents)
    }

    override fun onCleared() {
        super.onCleared()
        stateMachine.clear()
    }

    private fun fetchEditorials(): Observable<EditorialResult> =
            editorialInteractor.fetchEditorials().applyMvi({ EditorialResult.Success(it) },
                    { EditorialResult.Error(it) }, { EditorialResult.InProgress })

}
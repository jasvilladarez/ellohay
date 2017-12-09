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
import io.github.jasvilladarez.domain.repository.editorial.EditorialRepository
import io.github.jasvilladarez.ello.common.MviStateMachine
import io.github.jasvilladarez.ello.common.MviViewModel
import io.github.jasvilladarez.ello.util.applyMvi
import io.reactivex.Observable

internal class EditorialViewModel(
        private val editorialRepository: EditorialRepository
) : ViewModel(), MviViewModel<EditorialIntent, EditorialViewState> {

    private var nextItem: Int? = null

    private val stateMachine =
            MviStateMachine<EditorialIntent, EditorialResult, EditorialViewState>(EditorialViewState(), {
                when (it) {
                    is EditorialIntent.Load -> fetchEditorials()
                    is EditorialIntent.LoadMore -> fetchEditorials(nextItem)
                }
            }, { state, result ->
                when (result) {
                    is EditorialResult.Success -> {
                        nextItem = result.editorialStream.next
                        state.copy(
                                editorials = result.editorialStream.editorials,
                                isLoading = false, errorMessage = null)
                    }
                    is EditorialResult.Error -> state.copy(isLoading = false, errorMessage = null)
                    is EditorialResult.InProgress -> state.copy(isLoading = true)
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

    private fun fetchEditorials(nextItem: Int? = null): Observable<EditorialResult> =
            editorialRepository.fetchEditorials(nextItem).applyMvi({ EditorialResult.Success(it) },
                    { EditorialResult.Error(it) }, { EditorialResult.InProgress })

}
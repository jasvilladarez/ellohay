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

package io.github.jasvilladarez.ello.browse.editorial

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.jasvilladarez.domain.repository.browse.BrowseRepository
import io.github.jasvilladarez.ello.common.MviStateMachine
import io.github.jasvilladarez.ello.common.MviViewModel
import io.github.jasvilladarez.ello.util.applyMvi
import io.reactivex.Observable

internal class EditorialViewModel(
        private val browseRepository: BrowseRepository
) : ViewModel(), MviViewModel<EditorialIntent, EditorialViewState> {

    private val stateMachine =
            MviStateMachine<EditorialIntent, EditorialResult, EditorialViewState>(
                    EditorialViewState.DefaultView(), {
                when (it) {
                    is EditorialIntent.Load -> fetchEditorials()
                    is EditorialIntent.LoadMore -> fetchEditorials(it.nextPageId)
                    is EditorialIntent.ItemClick -> {
                        //FIXME Change this to open viewing of editorial page
                        Observable.just(EditorialResult.InProgress(EditorialResult.EditorialMode.LOAD))
                    }
                }
            }, { _, result ->
                when (result) {
                    is EditorialResult.Success -> when (result.mode) {
                        EditorialResult.EditorialMode.LOAD ->
                            EditorialViewState.DefaultView(result.editorialStream.editorials,
                                    result.editorialStream.next)
                        EditorialResult.EditorialMode.LOAD_MORE ->
                            EditorialViewState.MoreView(result.editorialStream.editorials,
                                    result.editorialStream.next)
                    }
                    is EditorialResult.Error -> EditorialViewState.ErrorView(result.error.message)
                    is EditorialResult.InProgress -> when (result.mode) {
                        EditorialResult.EditorialMode.LOAD -> EditorialViewState.InitialLoadingView
                        EditorialResult.EditorialMode.LOAD_MORE -> EditorialViewState.MoreLoadingView
                    }
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

    private fun fetchEditorials(nextPageId: String? = null): Observable<EditorialResult> =
            browseRepository.fetchEditorials(nextPageId).applyMvi(
                    {
                        EditorialResult.Success(it, nextPageId?.let {
                            EditorialResult.EditorialMode.LOAD_MORE
                        } ?: EditorialResult.EditorialMode.LOAD)
                    },
                    { EditorialResult.Error(it) },
                    {
                        EditorialResult.InProgress(nextPageId?.let {
                            EditorialResult.EditorialMode.LOAD_MORE
                        } ?: EditorialResult.EditorialMode.LOAD)
                    })

}
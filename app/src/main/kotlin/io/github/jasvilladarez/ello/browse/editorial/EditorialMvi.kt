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

import io.github.jasvilladarez.domain.entity.Editorial
import io.github.jasvilladarez.domain.entity.EditorialStream
import io.github.jasvilladarez.ello.common.MviIntent
import io.github.jasvilladarez.ello.common.MviResult
import io.github.jasvilladarez.ello.common.MviViewState

internal sealed class EditorialIntent : MviIntent {

    object Load : EditorialIntent()

    data class LoadMore(
            val nextPageId: String?
    ) : EditorialIntent()

    data class ItemClick(
            val item: Editorial
    ) : EditorialIntent()

}

internal sealed class EditorialResult : MviResult {

    enum class EditorialMode {
        LOAD, LOAD_MORE
    }

    data class Success(
            val editorialStream: EditorialStream = EditorialStream(emptyList()),
            val mode: EditorialMode
    ) : EditorialResult()

    data class Error(
            val error: Throwable
    ) : EditorialResult()

    data class InProgress(
            val mode: EditorialMode
    ) : EditorialResult()

}

internal sealed class EditorialViewState : MviViewState {

    data class DefaultView(
            val editorials: List<Editorial> = emptyList(),
            val nextPageId: String? = null
    ) : EditorialViewState()

    data class MoreView(
            val editorials: List<Editorial> = emptyList(),
            val nextPageId: String? = null
    ) : EditorialViewState()

    object InitialLoadingView : EditorialViewState()

    object MoreLoadingView : EditorialViewState()

    data class ErrorView(
            val errorMessage: String?
    ) : EditorialViewState()

}
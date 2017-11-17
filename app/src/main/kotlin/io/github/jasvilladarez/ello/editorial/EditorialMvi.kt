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

import io.github.jasvilladarez.ello.common.MviAction
import io.github.jasvilladarez.ello.common.MviIntent
import io.github.jasvilladarez.ello.common.MviResult
import io.github.jasvilladarez.ello.common.MviViewState

internal sealed class EditorialIntent : MviIntent {

    object InitialIntent : EditorialIntent()
}

internal sealed class EditorialAction : MviAction {

    data class LoadEditorialAction(
            val forcedUpdate: Boolean = false
    ) : EditorialAction()
}

internal sealed class EditorialResult : MviResult

internal sealed class EditorialViewState : MviViewState {

    data class View(
            val isLoading: Boolean = false
    ) : EditorialViewState()
}
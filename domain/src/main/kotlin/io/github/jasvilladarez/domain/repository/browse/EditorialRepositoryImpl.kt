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

package io.github.jasvilladarez.domain.repository.browse

import android.net.Uri
import io.github.jasvilladarez.domain.entity.EditorialStream
import io.github.jasvilladarez.domain.util.applySchedulers
import io.reactivex.Observable

internal class EditorialRepositoryImpl(
        private val editorialApi: EditorialApi
) : EditorialRepository {

    override fun fetchEditorials(nextPageId: Int?): Observable<EditorialStream> {
        return editorialApi.fetchEditorials(nextPageId).map {
            val next = Uri.parse(it.headers().get("link")?.substringBefore(">")
                    ?.substringAfter("<")).getQueryParameter("before")
            it.body()?.apply { this.next = next?.toInt() } ?: EditorialStream(emptyList())
        }.toObservable().applySchedulers()
    }
}
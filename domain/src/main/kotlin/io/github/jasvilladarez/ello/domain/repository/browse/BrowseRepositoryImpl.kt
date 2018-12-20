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

package io.github.jasvilladarez.ello.domain.repository.browse

import io.github.jasvilladarez.ello.domain.entity.*
import io.github.jasvilladarez.ello.domain.util.applySchedulers
import io.github.jasvilladarez.ello.domain.util.getParameterInLink
import io.github.jasvilladarez.ello.domain.util.toObservable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.runBlocking
import retrofit2.Response

internal class BrowseRepositoryImpl(
        private val browseApi: BrowseApi
) : BrowseRepository {

    override fun fetchEditorials(nextPageId: String?): Observable<EditorialStream> {
        return runBlocking {
            tryFetchingEditorials(nextPageId)
        }.toObservable().map {
            val next = it.headers().getParameterInLink("before")
            it.body()?.apply { this.next = next }
                    ?: EditorialStream(emptyList())
        }.applySchedulers()
    }

    private fun tryFetchingEditorials(nextPageId: String?): Response<EditorialStream> {
        return browseApi.fetchEditorials(nextPageId).subscribeOn(Schedulers.io()).blockingGet()
    }

    override fun fetchArtistInvites(nextPageId: String?): Observable<ArtistInviteStream> {
        return browseApi.fetchArtistInvites(nextPageId).map {
            val next = it.headers().getParameterInLink("page")
            it.body()?.apply { this.next = next }
                    ?: ArtistInviteStream(emptyList())
        }.toObservable().applySchedulers()
    }

    override fun fetchCategories(): Observable<List<Category>> {
        return browseApi.fetchCategories(true).map { it.body()?.categories ?: emptyList() }
                .toObservable().applySchedulers()
    }

    override fun fetchPostsByCategory(categorySlug: String, nextPageId: String?): Observable<PostStream> {
        return (when (categorySlug) {
            Category.SLUG_FEATURED -> browseApi.fetchFeaturedPosts(nextPageId)
            Category.SLUG_TRENDING -> browseApi.fetchTrendingPosts(nextPageId?.toInt())
            Category.SLUG_RECENT -> browseApi.fetchRecentPosts(nextPageId)
            else -> browseApi.fetchPostsInCategory(categorySlug, nextPageId)
        }).map {
            val next = if (categorySlug == Category.SLUG_TRENDING) it.headers()
                    .getParameterInLink("page")
            else it.headers().getParameterInLink("before")
            it.body()?.apply { this.next = next } ?: PostStream(Linked(), emptyList())
        }.toObservable().applySchedulers()
    }
}
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

import io.github.jasvilladarez.domain.entity.ArtistInviteStream
import io.github.jasvilladarez.domain.entity.Category
import io.github.jasvilladarez.domain.entity.EditorialStream
import io.github.jasvilladarez.domain.entity.PostStream
import io.github.jasvilladarez.domain.util.applySchedulers
import io.github.jasvilladarez.domain.util.getParameterInLink
import io.reactivex.Observable

internal class BrowseRepositoryImpl(
        private val browseApi: BrowseApi
) : BrowseRepository {

    override fun fetchEditorials(nextPageId: Int?): Observable<EditorialStream> {
        return browseApi.fetchEditorials(nextPageId).map {
            val next = it.headers().getParameterInLink("before")
            it.body()?.apply { this.next = next?.toIntOrNull() }
                    ?: EditorialStream(emptyList())
        }.toObservable().applySchedulers()
    }

    override fun fetchArtistInvites(nextPageId: Int?): Observable<ArtistInviteStream> {
        return browseApi.fetchArtistInvites(nextPageId).map {
            val next = it.headers().getParameterInLink("page")
            it.body()?.apply { this.next = next?.toIntOrNull() }
                    ?: ArtistInviteStream(emptyList())
        }.toObservable().applySchedulers()
    }

    override fun fetchCategories(): Observable<List<Category>> {
        return browseApi.fetchCategories(true).map { it.body()?.categories ?: emptyList() }
                .toObservable().applySchedulers()
    }

    override fun fetchPostsByCategory(category: Category, nextPageId: String?): Observable<PostStream> {
        return (when (category.slug) {
            Category.SLUG_FEATURED -> browseApi.fetchFeaturedPosts(nextPageId)
            Category.SLUG_TRENDING -> browseApi.fetchTrendingPosts(nextPageId?.toInt())
            Category.SLUG_RECENT -> browseApi.fetchFeaturedPosts(nextPageId)
            else -> browseApi.fetchPostsInCategory(category.slug, nextPageId)
        }).map {
            val next = if (category.slug == Category.SLUG_TRENDING) it.headers()
                    .getParameterInLink("page")
            else it.headers().getParameterInLink("before")
            it.body()?.apply { this.next = next } ?: PostStream(emptyList())
        }.toObservable().applySchedulers()
    }
}
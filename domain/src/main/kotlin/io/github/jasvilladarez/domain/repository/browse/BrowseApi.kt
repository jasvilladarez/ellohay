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
import io.github.jasvilladarez.domain.entity.CategoryStream
import io.github.jasvilladarez.domain.entity.EditorialStream
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface BrowseApi {

    /**
     * Fetch editorials
     *
     * @param before - before ID from Link header
     */
    @GET("editorials")
    fun fetchEditorials(@Query("before") before: Int? = null)
            : Single<Response<EditorialStream>>

    /**
     * Fetch artist invites
     *
     * @param page - page ID from Link header
     */
    @GET("artist_invites")
    fun fetchArtistInvites(@Query("page") page: Int? = null)
            : Single<Response<ArtistInviteStream>>

    /**
     * Fetch categories
     *
     * @param meta - include 'meta' categories such as 'featured', 'trending' and 'recent'
     * @param all - include inactive categories
     */
    @GET("categories")
    fun fetchCategories(@Query("meta") meta: Boolean? = null,
                        @Query("all") all: Boolean? = null)
            : Single<Response<CategoryStream>>
}
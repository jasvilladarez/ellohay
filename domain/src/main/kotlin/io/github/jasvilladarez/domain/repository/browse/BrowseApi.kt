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
import io.github.jasvilladarez.domain.entity.PostStream
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface BrowseApi {

    /**
     * Fetch editorials
     *
     * @param before - page ID from Link header
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

    /**
     * Fetch posts in each category
     *
     * @param slug - The url `slug` property of the category desired.
     * @param before - The pagination cursor, returned in the `link` header of the previous page.
     * @param limit - Number of posts to return per page. Default: 25
     */
    @GET("categories/{slug}/posts/recent")
    fun fetchPostsInCategory(@Path("slug") slug: String,
                             @Query("before") before: String? = null,
                             @Query("per_page") limit: Int? = null): Single<PostStream>

    /**
     * Fetch ll the newest posts across all the categories  - time ordered.
     *
     * @param before - The pagination cursor, returned in the `link` header of the previous page.
     * @param limit - Number of posts to return per page. Default: 25
     */
    @GET("categories/posts/recent")
    fun fetchFeaturedPosts(@Query("before") before: String? = null,
                           @Query("per_page") limit: Int? = null): Single<PostStream>

    /**
     * Fetch all the newest posts across the network - time ordered.
     *
     * @param before - The pagination cursor, returned in the `link` header of the previous page.
     * @param limit - Number of posts to return per page. Default: 25
     */
    @GET("discover/posts/recent")
    fun fetchRecentPosts(@Query("before") before: String? = null,
                         @Query("per_page") limit: Int? = null): Single<PostStream>

    /**
     * Fetch trending across the entire network.
     *
     * @param before - What page to retrieve
     * @param limit - Number of posts to return per page. Default: 25
     * @param imagesOnly - Flag to return only posts with images
     */
    @GET("discover/posts/trending")
    fun fetchTrendingPosts(@Query("page") page: Int? = null,
                           @Query("per_page") limit: Int? = null,
                           @Query("images_only") imagesOnly: Boolean = true): Single<PostStream>
}
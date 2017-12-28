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

package io.github.jasvilladarez.ello.browse.discover

import io.github.jasvilladarez.domain.entity.Category
import io.github.jasvilladarez.domain.entity.Post
import io.github.jasvilladarez.domain.entity.PostStream
import io.github.jasvilladarez.ello.common.MviIntent
import io.github.jasvilladarez.ello.common.MviResult
import io.github.jasvilladarez.ello.common.MviViewState

internal sealed class DiscoverIntent : MviIntent {

    object LoadCategories : DiscoverIntent()

    data class LoadPosts(
            val selectedItem: Category,
            val nextPageId: String? = null
    ) : DiscoverIntent()
}

internal sealed class DiscoverResult : MviResult {

    enum class DiscoverMode {
        LOAD_CATEGORIES, LOAD_POSTS
    }

    data class SuccessCategories(
            val categories: List<Category> = emptyList()
    ) : DiscoverResult()

    data class SuccessPosts(
            val postStream: PostStream = PostStream(emptyList())
    ) : DiscoverResult()

    data class Error(
            val error: Throwable
    ) : DiscoverResult()

    data class InProgress(
            val mode: DiscoverMode
    ) : DiscoverResult()
}

internal sealed class DiscoverViewState : MviViewState {

    data class DefaultCategoryView(
            val categories: List<Category> = emptyList()
    ) : DiscoverViewState()

    data class DefaultPostsView(
            val posts: List<Post> = emptyList(),
            val nextPageId: String? = null
    ) : DiscoverViewState()

    data class ErrorView(
            val errorMessage: String?
    ) : DiscoverViewState()

    object LoadingCategoriesView : DiscoverViewState()

    object LoadingPostsView : DiscoverViewState()
}
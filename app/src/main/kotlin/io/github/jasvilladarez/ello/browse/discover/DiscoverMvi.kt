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

package io.github.jasvilladarez.ello.browse.discover

import io.github.jasvilladarez.ello.common.MviIntent
import io.github.jasvilladarez.ello.common.MviResult
import io.github.jasvilladarez.ello.common.MviViewState

internal sealed class DiscoverIntent : MviIntent {

    object LoadCategories : DiscoverIntent()

    data class LoadPosts(
            val selectedItem: CategoryItem
    ) : DiscoverIntent()

    data class LoadMorePosts(
            val selectedItem: CategoryItem,
            val nextPageId: String?
    ) : DiscoverIntent()
}

internal sealed class DiscoverResult : MviResult {

    enum class DiscoverMode {
        LOAD_POSTS, LOAD_MORE_POSTS
    }

    data class SuccessCategories(
            val categories: List<CategoryItem> = emptyList()
    ) : DiscoverResult()

    data class SuccessPosts(
            val posts: List<PostItem> = emptyList(),
            val nextPageId: String? = null,
            val mode: DiscoverMode
    ) : DiscoverResult()

    data class Error(
            val error: Throwable
    ) : DiscoverResult()

    object InProgressCategories : DiscoverResult()

    data class InProgressPosts(
            val mode: DiscoverMode
    ) : DiscoverResult()
}

internal sealed class DiscoverViewState : MviViewState {

    data class DefaultCategoryView(
            val categories: List<CategoryItem> = emptyList()
    ) : DiscoverViewState()

    data class DefaultPostsView(
            val posts: List<PostItem> = emptyList(),
            val nextPageId: String? = null
    ) : DiscoverViewState()

    data class MorePostsView(
            val posts: List<PostItem> = emptyList(),
            val nextPageId: String? = null
    ) : DiscoverViewState()

    data class ErrorView(
            val errorMessage: String?
    ) : DiscoverViewState()

    object LoadingCategoriesView : DiscoverViewState()

    object LoadingPostsView : DiscoverViewState()

    object LoadingMorePostsView : DiscoverViewState()
}
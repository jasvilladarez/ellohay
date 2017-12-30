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

import io.github.jasvilladarez.domain.entity.*
import io.github.jasvilladarez.ello.util.fromHtml

internal data class CategoryItem(
        val name: String,
        val slug: String,
        val imageUrl: String?,
        var isSelected: Boolean = false
)

internal fun Category.mapToViewItem(): CategoryItem = CategoryItem(
        name,
        slug,
        image.original?.url
)

internal data class PostItem(
        val authorUsername: String?,
        val authorImageUrl: String?,
        val summary: List<PostBlockItem>?
)

internal interface PostBlockItem

internal data class ImagePostBlockItem(
        val imageUrl: String?
) : PostBlockItem

internal data class TextPostBlockItem(
        val text: CharSequence?
) : PostBlockItem

internal data class EmbedPostBlockItem(
        val embedUrl: String?
) : PostBlockItem

internal fun PostStream.mapToViewItems(): List<PostItem> = posts.map { post ->
    val author = this.linked.users?.firstOrNull { it.id == post.authorId }
    PostItem(
            author?.username,
            author?.avatar?.mdpi?.url,
            post.summary?.mapToViewItems()
    )
}

internal fun List<PostBlock>.mapToViewItems(): List<PostBlockItem> = this.mapNotNull {
    when (it) {
        is TextPostBlock -> TextPostBlockItem(it.text.fromHtml())
        is ImagePostBlock -> ImagePostBlockItem(it.image.url)
        is EmbedPostBlock -> EmbedPostBlockItem(it.data.url)
        else -> null
    }
}
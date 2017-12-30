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

package io.github.jasvilladarez.domain.entity

import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
import io.github.jasvilladarez.domain.util.RuntimeTypeAdapterFactory

abstract class PostBlock(
        val kind: Kind
) {
    enum class Kind {
        TEXT, IMAGE, EMBED
    }
}

data class ImagePostBlock(
        @SerializedName("data")
        val image: ImageVersion,
        @SerializedName("links")
        val links: List<Link>? = null
) : PostBlock(Kind.IMAGE)

data class TextPostBlock(
        @SerializedName("data")
        val text: String
) : PostBlock(Kind.TEXT)

data class EmbedPostBlock(
        @SerializedName("data")
        val data: EmbedData
) : PostBlock(Kind.EMBED)

data class EmbedData(
        @SerializedName("url")
        val url: String
)

internal fun postBlockTypeAdapter(): TypeAdapterFactory = RuntimeTypeAdapterFactory
        .of(PostBlock::class, "kind")
        .registerSubtype(TextPostBlock::class, "text")
        .registerSubtype(ImagePostBlock::class, "image")
        .registerSubtype(EmbedPostBlock::class, "embed")
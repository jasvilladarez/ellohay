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

import com.google.gson.annotations.SerializedName

data class Editorial(
        @SerializedName("id")
        val id: Long,
        @SerializedName("kind")
        val kind: EditorialType,
        @SerializedName("title")
        val title: String,
        @SerializedName("subtitle")
        val subtitle: String? = null,
        /**
         * Html formatted version of the subtitle
         */
        @SerializedName("rendered_subtitle")
        val renderedSubtitle: String? = null,
        @SerializedName("two_by_two_image")
        val image: Image? = null,
        @SerializedName("links")
        val links: List<Link>? = null
) {

    enum class EditorialType {
        POST, INTERNAL, EXTERNAL, POST_STREAM
    }
}
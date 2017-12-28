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

import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class Links {
    var links: List<Link>? = null
}

interface Link

data class PostLink(
        @SerializedName("id")
        val id: String,
        @SerializedName("href")
        val href: String
) : Link

data class PostStreamLink(
        @SerializedName("href")
        val href: String
) : Link

data class AuthorLink(
        @SerializedName("id")
        val id: Long
) : Link

internal class LinksTypeAdapter : TypeAdapter<Links>() {

    override fun read(reader: JsonReader?): Links? {
        return reader?.takeUnless { it.peek() === JsonToken.NULL }?.let {
            val links = mutableListOf<Link>()
            it.beginObject()
            while (it.hasNext()) {
                when (it.nextName()) {
                    "post" -> links.add(readPost(it))
                    "post_stream" -> links.add(readPostStream(it))
                    "author" -> links.add(readAuthor(it))
                }
            }
            it.endObject()
            Links().apply { this.links = links }
        } ?: let {
            reader?.nextNull()
            null
        }
    }

    override fun write(out: JsonWriter?, value: Links?) {

    }

    private fun readPost(reader: JsonReader): PostLink {
        var id = ""
        var href = ""
        reader.beginObject()
        while (reader.peek() != JsonToken.END_OBJECT) {
            if (reader.peek() != JsonToken.NAME) {
                reader.skipValue()
                continue
            }
            when (reader.nextName()) {
                "id" -> id = reader.nextString()
                "href" -> href = reader.nextString()
            }
        }
        reader.endObject()
        return PostLink(id, href)
    }

    private fun readPostStream(reader: JsonReader): PostStreamLink {
        var href = ""
        reader.beginObject()
        while (reader.peek() != JsonToken.END_OBJECT) {
            if (reader.peek() != JsonToken.NAME) {
                reader.skipValue()
                continue
            }
            when (reader.nextName()) {
                "href" -> href = reader.nextString()
            }
        }
        reader.endObject()
        return PostStreamLink(href)
    }

    private fun readAuthor(reader: JsonReader): AuthorLink {
        var id = 0L
        reader.beginObject()
        while (reader.peek() != JsonToken.END_OBJECT) {
            if (reader.peek() != JsonToken.NAME) {
                reader.skipValue()
                continue
            }
            when (reader.nextName()) {
                "id" -> id = reader.nextLong()
            }
        }
        reader.endObject()
        return AuthorLink(id)
    }
}
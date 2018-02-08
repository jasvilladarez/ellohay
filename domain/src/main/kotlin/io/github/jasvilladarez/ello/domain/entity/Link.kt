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

package io.github.jasvilladarez.ello.domain.entity

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.github.jasvilladarez.ello.domain.util.MapToListTypeAdapterFactory

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

data class RepostedAuthorLink(
        @SerializedName("id")
        val id: Long
) : Link

class CategoryLink : ArrayList<Long>(), Link

interface AssetsLink : Link

data class AssetLink(
        val assetId: Long
) : AssetsLink

class AssetsListLink : ArrayList<Long>(), AssetsLink

internal fun linksTypeAdapter(): TypeAdapterFactory = MapToListTypeAdapterFactory
        .of(Link::class)
        .registerSubtype(PostLink::class, "post")
        .registerSubtype(PostStreamLink::class, "post_stream")
        .registerSubtype(AuthorLink::class, "author")
        .registerSubtype(RepostedAuthorLink::class, "reposted_author")
        .registerSubtype(CategoryLink::class, "categories")
        .registerSubtype(AssetsLink::class, "assets")

internal fun assetsLinkTypeAdapter(): TypeAdapterFactory = object : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>?): TypeAdapter<T>? {
        return if (type?.rawType == AssetsLink::class.java) {
            val assetsLinkDelegate = gson.getDelegateAdapter(this,
                    TypeToken.get(AssetsListLink::class.java))

            object : TypeAdapter<T>() {
                override fun read(reader: JsonReader?): T? {
                    val jsonElement = Streams.parse(reader)
                    return when (jsonElement) {
                        is JsonPrimitive -> AssetLink(jsonElement.asLong) as T
                        is JsonArray -> assetsLinkDelegate.fromJsonTree(jsonElement) as T
                        else -> null
                    }
                }

                override fun write(out: JsonWriter?, value: T) {
                    out?.nullValue()
                }
            }
        } else {
            null
        }
    }

}
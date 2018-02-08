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

package io.github.jasvilladarez.ello.domain.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import io.github.jasvilladarez.ello.domain.entity.assetsLinkTypeAdapter
import io.github.jasvilladarez.ello.domain.entity.linksTypeAdapter
import io.github.jasvilladarez.ello.domain.entity.postBlockTypeAdapter
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass

internal val gson: Gson = GsonBuilder()
        .registerTypeAdapterFactory(LowercaseEnumTypeAdapterFactory())
        .registerTypeAdapterFactory(assetsLinkTypeAdapter())
        .registerTypeAdapterFactory(linksTypeAdapter())
        .registerTypeAdapterFactory(postBlockTypeAdapter())
        .create()

internal fun <T : Any> T.toJson(): String =
        gson.toJson(this)

internal fun <T : Any> String.fromJson(kClazz: KClass<T>): T =
        gson.fromJson(this, kClazz.java)

internal class LowercaseEnumTypeAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType as Class<T>
        if (!rawType.isEnum) {
            return null
        }

        val lowercaseToConstant = HashMap<String, T>()
        for (constant in rawType.enumConstants) {
            lowercaseToConstant.put(toLowercase(constant), constant)
        }

        return object : TypeAdapter<T>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T?) {
                value?.let {
                    out.value(toLowercase(it))
                } ?: out.nullValue()
            }

            @Throws(IOException::class)
            override fun read(reader: JsonReader): T? {
                return if (reader.peek() === JsonToken.NULL) {
                    reader.nextNull()
                    null
                } else {
                    lowercaseToConstant[reader.nextString()]
                }
            }
        }
    }

    private fun toLowercase(o: Any?): String = o.toString().toLowerCase(Locale.US)
}
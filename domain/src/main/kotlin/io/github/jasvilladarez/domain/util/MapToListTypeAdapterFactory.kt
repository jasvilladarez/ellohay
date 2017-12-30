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

package io.github.jasvilladarez.domain.util

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

internal class MapToListTypeAdapterFactory<T : Any> private constructor(
        private val baseType: KClass<T>
) : TypeAdapterFactory {

    private val labelToSubtype: LinkedHashMap<String, KClass<*>> = linkedMapOf()
    private val subtypeToLabel: LinkedHashMap<KClass<*>, String> = linkedMapOf()

    companion object {
        fun <T : Any> of(type: KClass<T>) = MapToListTypeAdapterFactory(type)
    }

    fun <R : T> registerSubtype(type: KClass<R>, label: String): MapToListTypeAdapterFactory<T> {
        if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
            throw IllegalArgumentException("Types and Labels must be unique")
        }
        labelToSubtype.put(label, type)
        subtypeToLabel.put(type, label)
        return this
    }

    override fun <R> create(gson: Gson, type: TypeToken<R>?): TypeAdapter<R>? {
        val listType = (type?.type as? ParameterizedType)?.actualTypeArguments?.get(0)
        if (!(type?.rawType == List::class.java && listType == baseType.java))
            return null

        val labelToDelegate: LinkedHashMap<String, TypeAdapter<*>> = linkedMapOf()
        val subtypeToDelegate: LinkedHashMap<KClass<*>, TypeAdapter<*>> = linkedMapOf()

        labelToSubtype.entries.forEach {
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(it.value.java))
            labelToDelegate.put(it.key, delegate)
            subtypeToDelegate.put(it.value, delegate)
        }

        return object : TypeAdapter<List<T>>() {
            override fun read(reader: JsonReader?): List<T>? {
                val list = mutableListOf<T>()
                val jsonElement = Streams.parse(reader)
                jsonElement.asJsonObject.entrySet().forEach { jsonObject ->
                    val delegate = labelToDelegate[jsonObject.key]
                    delegate?.let {
                        list.add(it.fromJsonTree(jsonObject.value) as T)
                    }
                }
                return list
            }

            override fun write(out: JsonWriter?, value: List<T>) {
                out?.nullValue()
            }
        }.nullSafe() as? TypeAdapter<R>
    }

}
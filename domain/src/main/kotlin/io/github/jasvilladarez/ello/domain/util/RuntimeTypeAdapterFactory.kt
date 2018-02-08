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

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass

/**
 * Adapts values whose runtime type may differ from their declaration type. This
 * is necessary when a field's type is not the same type that GSON should create
 * when deserializing that field. For example, consider these types:
 * <pre>   {@code
 *   abstract class Shape {
 *     int x;
 *     int y;
 *   }
 *   class Circle extends Shape {
 *     int radius;
 *   }
 *   class Rectangle extends Shape {
 *     int width;
 *     int height;
 *   }
 *   class Diamond extends Shape {
 *     int width;
 *     int height;
 *   }
 *   class Drawing {
 *     Shape bottomShape;
 *     Shape topShape;
 *   }
 * }</pre>
 * <p>Without additional type information, the serialized JSON is ambiguous. Is
 * the bottom shape in this drawing a rectangle or a diamond? <pre>   {@code
 *   {
 *     "bottomShape": {
 *       "width": 10,
 *       "height": 5,
 *       "x": 0,
 *       "y": 0
 *     },
 *     "topShape": {
 *       "radius": 2,
 *       "x": 4,
 *       "y": 1
 *     }
 *   }}</pre>
 * This class addresses this problem by adding type information to the
 * serialized JSON and honoring that type information when the JSON is
 * deserialized: <pre>   {@code
 *   {
 *     "bottomShape": {
 *       "type": "Diamond",
 *       "width": 10,
 *       "height": 5,
 *       "x": 0,
 *       "y": 0
 *     },
 *     "topShape": {
 *       "type": "Circle",
 *       "radius": 2,
 *       "x": 4,
 *       "y": 1
 *     }
 *   }}</pre>
 * Both the type field name ({@code "type"}) and the type labels ({@code
 * "Rectangle"}) are configurable.
 *
 * <h3>Registering Types</h3>
 * Create a {@code RuntimeTypeAdapterFactory} by passing the base type and type field
 * name to the {@link #of} factory method. If you don't supply an explicit type
 * field name, {@code "type"} will be used. <pre>   {@code
 *   RuntimeTypeAdapterFactory<Shape> shapeAdapterFactory
 *       = RuntimeTypeAdapterFactory.of(Shape.class, "type");
 * }</pre>
 * Next register all of your subtypes. Every subtype must be explicitly
 * registered. This protects your application from injection attacks. If you
 * don't supply an explicit type label, the type's simple name will be used.
 * <pre>   {@code
 *   shapeAdapter.registerSubtype(Rectangle.class, "Rectangle");
 *   shapeAdapter.registerSubtype(Circle.class, "Circle");
 *   shapeAdapter.registerSubtype(Diamond.class, "Diamond");
 * }</pre>
 * Finally, register the type adapter factory in your application's GSON builder:
 * <pre>   {@code
 *   Gson gson = new GsonBuilder()
 *       .registerTypeAdapterFactory(shapeAdapterFactory)
 *       .create();
 * }</pre>
 * Like {@code GsonBuilder}, this API supports chaining: <pre>   {@code
 *   RuntimeTypeAdapterFactory<Shape> shapeAdapterFactory = RuntimeTypeAdapterFactory.of(Shape.class)
 *       .registerSubtype(Rectangle.class)
 *       .registerSubtype(Circle.class)
 *       .registerSubtype(Diamond.class);
 * }</pre>
 */
internal class RuntimeTypeAdapterFactory<T : Any> private constructor(
        private val baseType: KClass<T>,
        private val typeFieldName: String
) : TypeAdapterFactory {

    companion object {

        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         */
        fun <T : Any> of(baseType: KClass<T>, typeFieldName: String): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName)
        }

        /**
         * Creates a new runtime type adapter for `baseType` using `"type"` as
         * the type field name.
         */
        fun <T : Any> of(baseType: KClass<T>): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, "type")
        }
    }

    private val labelToSubtype = LinkedHashMap<String, KClass<*>>()
    private val subtypeToLabel = LinkedHashMap<KClass<*>, String>()

    /**
     * Registers `type` identified by `label`. Labels are case
     * sensitive.
     *
     * @throws IllegalArgumentException if either `type` or `label`
     * have already been registered on this type adapter.
     */
    fun registerSubtype(type: KClass<out T>, label: String? = type::class.java.simpleName): RuntimeTypeAdapterFactory<T> {
        if (label == null) {
            throw NullPointerException("Label should not be null")
        }
        if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
            throw IllegalArgumentException("types and labels must be unique")
        }
        labelToSubtype.put(label, type)
        subtypeToLabel.put(type, label)
        return this
    }

    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (type.rawType != baseType.java) return null

        val labelToDelegate: LinkedHashMap<String, TypeAdapter<*>> = linkedMapOf()
        val subtypeToDelegate: LinkedHashMap<KClass<*>, TypeAdapter<*>> = linkedMapOf()

        labelToSubtype.entries.forEach {
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(it.value.java))
            labelToDelegate.put(it.key, delegate)
            subtypeToDelegate.put(it.value, delegate)
        }

        return object : TypeAdapter<R>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): R? {
                val jsonElement = Streams.parse(`in`)
                val labelJsonElement = jsonElement.asJsonObject.remove(typeFieldName) ?:
                        throw JsonParseException("cannot deserialize " + baseType
                                + " because it does not define a field named " + typeFieldName)
                val label = labelJsonElement.asString
                val delegate = labelToDelegate[label] as? TypeAdapter<R> ?:
                        throw JsonParseException("cannot deserialize " + baseType + " subtype named "
                                + label + "; did you forget to register a subtype?")// registration requires that subtype extends T
                return delegate.fromJsonTree(jsonElement)
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: R) {
                val srcType = object : TypeToken<R>() {}::class
                val label = subtypeToLabel[srcType]
                val delegate = subtypeToDelegate[srcType] as? TypeAdapter<R> ?:
                        throw JsonParseException("cannot serialize " + srcType.simpleName
                                + "; did you forget to register a subtype?")// registration requires that subtype extends T
                val jsonObject = delegate.toJsonTree(value).asJsonObject
                if (jsonObject.has(typeFieldName)) {
                    throw JsonParseException("cannot serialize " + srcType.simpleName
                            + " because it already defines a field named " + typeFieldName)
                }
                val clone = JsonObject()
                clone.add(typeFieldName, JsonPrimitive(label))
                for ((key, value1) in jsonObject.entrySet()) {
                    clone.add(key, value1)
                }
                Streams.write(clone, out)
            }
        }.nullSafe()
    }
}
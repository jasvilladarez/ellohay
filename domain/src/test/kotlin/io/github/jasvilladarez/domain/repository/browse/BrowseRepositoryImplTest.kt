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

package io.github.jasvilladarez.domain.repository.browse

import io.github.jasvilladarez.domain.ApiFactory
import io.github.jasvilladarez.domain.createMockResponse
import io.github.jasvilladarez.domain.entity.*
import io.github.jasvilladarez.domain.readFromFile
import io.github.jasvilladarez.test.common.RxSpekRule
import io.github.jasvilladarez.test.common.addGroupRules
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

internal object BrowseRepositoryImplTest : Spek({
    addGroupRules(RxSpekRule())
    val mockServer by memoized { MockWebServer() }
    val baseUrl by memoized { mockServer.url("/").toString() }

    context("browse") {
        val browseApi by memoized { ApiFactory.createBrowseApi(Token.default(), baseUrl = baseUrl) }
        val browseRepositoryImpl by memoized { BrowseRepositoryImpl(browseApi) }

        on("fetchEditorials") {
            val observer by memoized { TestObserver<EditorialStream>() }
            mockServer.enqueue(createMockResponse(readFromFile("editorial_stream.json"),
                    "link: <https://ello.co/api/v2/editorials?before=101981&per_page=1>; rel=\"next\""))
            browseRepositoryImpl.fetchEditorials().subscribe(observer)

            it("should have 1 item in editorials") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual BrowseRepositoryTestObject.editorialStream
            }
        }

        on("fetchArtistInvites") {
            val observer by memoized { TestObserver<ArtistInviteStream>() }
            mockServer.enqueue(createMockResponse(readFromFile("artist_invite_stream.json"),
                    "link: <https://ello.co/api/v2/artist_invites?page=2&per_page=1>; rel=\"next\""))
            browseRepositoryImpl.fetchArtistInvites().subscribe(observer)

            it("should have 1 item in artist invites") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual BrowseRepositoryTestObject.artistInviteStream
            }
        }

        on("fetchCategories") {
            val observer by memoized { TestObserver<List<Category>>() }
            mockServer.enqueue(createMockResponse(readFromFile("category_list.json")))
            browseRepositoryImpl.fetchCategories().subscribe(observer)

            it("should have 1 item in list of categories") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual BrowseRepositoryTestObject.categories
            }
        }

        on("fetchPostsByCategory FEATURED") {
            val observer by memoized { TestObserver<PostStream>() }
            mockServer.enqueue(createMockResponse(readFromFile("post_stream.json"),
                    "link: <https://ello.co/api/v2/categories/posts/recent?before=123&per_page=1>; rel=\"next\""))
            browseRepositoryImpl.fetchPostsByCategory(Category.SLUG_FEATURED).subscribe(observer)

            it("should have 1 item in posts") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual BrowseRepositoryTestObject.postStream
            }
        }

        on(" fetchPostsByCategory TRENDING") {
            val observer by memoized { TestObserver<PostStream>() }
            mockServer.enqueue(createMockResponse(readFromFile("post_stream.json"),
                    "link: <https://ello.co/api/v2/categories/posts/recent?page=123&per_page=1>; rel=\"next\""))
            browseRepositoryImpl.fetchPostsByCategory(Category.SLUG_TRENDING).subscribe(observer)

            it("should have 1 item in posts") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual BrowseRepositoryTestObject.postStream
            }
        }

        on("fetchPostsByCategory RECENT") {
            val observer by memoized { TestObserver<PostStream>() }
            mockServer.enqueue(createMockResponse(readFromFile("post_stream.json"),
                    "link: <https://ello.co/api/v2/categories/posts/recent?before=123&per_page=1>; rel=\"next\""))
            browseRepositoryImpl.fetchPostsByCategory(Category.SLUG_RECENT).subscribe(observer)

            it("should have 1 item in posts") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual BrowseRepositoryTestObject.postStream
            }
        }

        on("fetchPostsByCategory other") {
            val observer by memoized { TestObserver<PostStream>() }
            mockServer.enqueue(createMockResponse(readFromFile("post_stream.json"),
                    "link: <https://ello.co/api/v2/categories/posts/recent?before=123&per_page=1>; rel=\"next\""))
            browseRepositoryImpl.fetchPostsByCategory("other").subscribe(observer)

            it("should have 1 item in posts") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual BrowseRepositoryTestObject.postStream
            }
        }
    }

    afterEachTest { mockServer.shutdown() }
})
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

package io.github.jasvilladarez.domain.repository.auth

import com.nhaarman.mockito_kotlin.whenever
import io.github.jasvilladarez.domain.ApiFactory
import io.github.jasvilladarez.domain.addRxScheduling
import io.github.jasvilladarez.domain.createMockResponse
import io.github.jasvilladarez.domain.entity.Token
import io.github.jasvilladarez.domain.preference.auth.AuthPreference
import io.github.jasvilladarez.domain.readFromFile
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

internal object AuthRepositoryImplTest : Spek({
    addRxScheduling()
    val mockServer by memoized { MockWebServer() }
    val baseUrl by memoized { mockServer.url("/").toString() }

    context("authentication") {
        val authApi by memoized { ApiFactory.createAuthApi(baseUrl) }
        val authPreference by memoized { mock(AuthPreference::class) }
        val token by memoized { Token.default() }
        val authRepository by memoized {
            AuthRepositoryImpl(authApi, authPreference, token)
        }

        on("fetchAccessToken empty preference") {
            val observer by memoized { TestObserver<Token>() }
            whenever(authPreference.token).thenReturn(null)
            mockServer.enqueue(createMockResponse(readFromFile("public_token.json")))
            authRepository.fetchAccessToken().subscribe(observer)

            it("should have a new token") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual AuthTokenTestObject.publicToken
                token shouldEqual AuthTokenTestObject.publicToken
            }
        }

        on("fetchAccessToken with expired token") {
            val observer by memoized { TestObserver<Token>() }
            val expiredToken by memoized {
                Token("bearer", "Expired token",
                        123456, 10)
            }
            whenever(authPreference.token).thenReturn(expiredToken)
            mockServer.enqueue(createMockResponse(readFromFile("public_token.json")))
            authRepository.fetchAccessToken(1234567000).subscribe(observer)

            it("should have a new token") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual AuthTokenTestObject.publicToken
                token shouldEqual AuthTokenTestObject.publicToken
            }
        }

        on("fetchAccessToken active token") {
            val observer by memoized { TestObserver<Token>() }
            val activeToken by memoized {
                Token("bearer", "Test token",
                        1517363305, 86400)
            }
            whenever(authPreference.token).thenReturn(activeToken)
            mockServer.enqueue(createMockResponse(readFromFile("public_token.json")))
            authRepository.fetchAccessToken(1517363400000).subscribe(observer)

            it("should have the active token") {
                observer.awaitTerminalEvent()
                observer.assertNoErrors()
                observer.assertNoTimeout()
                observer.assertComplete()

                observer.valueCount() shouldEqualTo 1
                observer.values().firstOrNull() shouldEqual activeToken
                token shouldEqual activeToken
            }
        }
    }

    afterEachTest { mockServer.shutdown() }
})
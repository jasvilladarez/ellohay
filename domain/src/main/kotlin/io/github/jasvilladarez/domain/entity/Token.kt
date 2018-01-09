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

data class Token(
        @SerializedName("token_type")
        private var _tokenType: String,
        @SerializedName("access_token")
        private var _accessToken: String,
        @SerializedName("created_at")
        private var _createdAt: Long,
        @SerializedName("expires_in")
        private var _expiresIn: Long,
        /**
         * Used to get a new token w/o username and password
         */
        @SerializedName("refresh_token")
        private var _refreshToken: String? = null
) {

    var tokenType: String
        get() = _tokenType
        private set(value) {
            _tokenType = value
        }

    var accessToken: String
        get() = _accessToken
        private set(value) {
            _accessToken = value
        }

    var createdAt: Long
        get() = _createdAt
        private set(value) {
            _createdAt = value
        }

    var expiresIn: Long
        get() = _expiresIn
        private set(value) {
            _expiresIn = value
        }

    var refreshToken: String?
        get() = _refreshToken
        private set(value) {
            _refreshToken = value
        }

    companion object {
        fun default(): Token = Token("", "",
                0, 0)
    }

    internal fun copy(token: Token) {
        tokenType = token.tokenType
        accessToken = token.accessToken
        createdAt = token.createdAt
        expiresIn = token.expiresIn
        refreshToken = token.refreshToken
    }
}
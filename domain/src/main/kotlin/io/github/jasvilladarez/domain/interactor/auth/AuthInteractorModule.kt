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

package io.github.jasvilladarez.domain.interactor.auth

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import io.github.jasvilladarez.domain.ApiFactory
import io.github.jasvilladarez.domain.entity.Token
import io.github.jasvilladarez.domain.preference.auth.AuthPreference
import io.github.jasvilladarez.domain.preference.auth.AuthPreferenceImpl
import io.github.jasvilladarez.ello.BuildConfig

@Module
class AuthInteractorModule {

    @Provides
    internal fun providesAuthApi(): AuthApi = ApiFactory.createApi(AuthApi::class.java,
            isDebug = BuildConfig.DEBUG)

    @Provides
    internal fun providesAuthPreference(sharedPreferences: SharedPreferences): AuthPreference =
            AuthPreferenceImpl(sharedPreferences)

    @Provides
    internal fun providesAuthInteractory(authApi: AuthApi,
                                         authPreference: AuthPreference,
                                         token: Token): AuthInteractor =
            AuthInteractorImpl(authApi, authPreference, token)
}
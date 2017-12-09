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

package io.github.jasvilladarez.ello.main

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.common.BaseActivity
import io.github.jasvilladarez.ello.common.MviView
import io.github.jasvilladarez.ello.discover.editorial.EditorialFragment
import io.github.jasvilladarez.ello.util.ui.setVisible
import io.github.jasvilladarez.ello.util.ui.showError
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

internal class MainActivity : BaseActivity(), MviView<MainIntent, MainViewState> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.state.observe(this, Observer {
            it?.let { render(it) }
        })
        viewModel.processIntents(intents())
    }

    override fun intents(): Observable<MainIntent> =
            loadIntent()

    override fun render(state: MainViewState) {
        when (state) {
            is MainViewState.View -> {
                fragmentContainer.setVisible(!state.isLoading)
                loadingView.setVisible(state.isLoading)
                if (state.isSuccessful) {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, EditorialFragment())
                            .commit()
                }
            }
            is MainViewState.Error -> {
                loadingView.setVisible(false)
                fragmentContainer.setVisible(false)
                showError(state.errorMessage)
            }
        }
    }

    private fun loadIntent(): Observable<MainIntent> = rxLifecycle.filter {
        it == Lifecycle.Event.ON_START
    }.map { MainIntent.Load }
}
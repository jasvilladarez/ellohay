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

package io.github.jasvilladarez.ello.browse.invites

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.common.BaseFragment
import io.github.jasvilladarez.ello.common.MviView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.ello_loading_list.*
import javax.inject.Inject

internal class ArtistInvitesFragment : BaseFragment(),
        MviView<ArtistInvitesIntent, ArtistInvitesViewState> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ArtistInvitesViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[ArtistInvitesViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.ello_loading_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(this, Observer {
            it?.let {
                render(it)
            }
        })
        viewModel.processIntents(intents())
    }

    override fun intents(): Observable<ArtistInvitesIntent> = loadIntent()

    override fun render(state: ArtistInvitesViewState) {
        when (state) {
            is ArtistInvitesViewState.DefaultView -> {

            }
            is ArtistInvitesViewState.LoadingView -> swipeRefreshLayout.isRefreshing = true
        }
    }

    private fun loadIntent(): Observable<ArtistInvitesIntent> = rxLifecycle.filter {
        it == Lifecycle.Event.ON_START
    }.map { ArtistInvitesIntent.Load }
}
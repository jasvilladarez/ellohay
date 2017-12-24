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

package io.github.jasvilladarez.ello.browse.editorial

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import io.github.jasvilladarez.domain.entity.Editorial
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.common.BaseFragment
import io.github.jasvilladarez.ello.common.adapter.ElloAdapter
import io.github.jasvilladarez.ello.common.MviView
import io.github.jasvilladarez.ello.util.ui.showError
import io.reactivex.Observable
import kotlinx.android.synthetic.main.ello_loading_list.*
import javax.inject.Inject

internal class EditorialFragment : BaseFragment(),
        MviView<EditorialIntent, EditorialViewState> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: EditorialViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[EditorialViewModel::class.java]
    }

    private val editorialAdapter: ElloAdapter<Editorial> by lazy {
        ElloAdapter(EditorialViewItem())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? =
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

    override fun intents(): Observable<EditorialIntent> = Observable.merge(
            loadIntent(), refreshIntent(), loadMoreIntent(), itemClickIntent()
    )

    override fun render(state: EditorialViewState) {
        when (state) {
            is EditorialViewState.DefaultView -> {
                swipeRefreshLayout.isRefreshing = false
                recyclerView.adapter ?: let {
                    recyclerView.adapter = editorialAdapter
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                }
                editorialAdapter.nextPageId = state.nextPageId
                editorialAdapter.items = state.editorials
            }
            is EditorialViewState.MoreView -> {
                editorialAdapter.isLoading = false
                editorialAdapter.nextPageId = state.nextPageId
                editorialAdapter.addItems(state.editorials)
            }
            is EditorialViewState.ErrorView -> {
                swipeRefreshLayout.isRefreshing = false
                editorialAdapter.isLoading = false
                context?.showError(state.errorMessage)
            }
            is EditorialViewState.InitialLoadingView -> swipeRefreshLayout.isRefreshing = true
            is EditorialViewState.MoreLoadingView -> editorialAdapter.isLoading = true
        }
    }

    private fun loadIntent(): Observable<EditorialIntent> = rxLifecycle.filter {
        it == Lifecycle.Event.ON_START
    }.map { EditorialIntent.Load }

    private fun refreshIntent(): Observable<EditorialIntent> = RxSwipeRefreshLayout
            .refreshes(swipeRefreshLayout).map { EditorialIntent.Load }

    private fun loadMoreIntent(): Observable<EditorialIntent> = editorialAdapter.onLoadMore()
            .map { EditorialIntent.LoadMore(editorialAdapter.nextPageId) }

    private fun itemClickIntent(): Observable<EditorialIntent> = editorialAdapter.onItemClick()
            .map { EditorialIntent.ItemClick(it) }
}
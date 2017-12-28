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

package io.github.jasvilladarez.ello.browse.discover

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
import io.github.jasvilladarez.domain.entity.Category
import io.github.jasvilladarez.domain.entity.Post
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.common.BaseFragment
import io.github.jasvilladarez.ello.common.MviView
import io.github.jasvilladarez.ello.common.adapter.ElloAdapter
import io.github.jasvilladarez.ello.util.ui.showError
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject

internal class DiscoverFragment : BaseFragment(), MviView<DiscoverIntent, DiscoverViewState> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: DiscoverViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[DiscoverViewModel::class.java]
    }

    private val categoriesAdapter: ElloAdapter<Category> by lazy {
        ElloAdapter(CategoryViewItem())
    }

    private val postAdapter: ElloAdapter<Post> by lazy {
        ElloAdapter(PostViewItem())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_discover, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(this, Observer {
            it?.let {
                render(it)
            }
        })
        viewModel.processIntents(intents())
    }

    override fun intents(): Observable<DiscoverIntent> = Observable.merge(
            loadCategoryIntent(),
            loadPostsIntent(),
            refreshIntent()
    )

    override fun render(state: DiscoverViewState) {
        when (state) {
            is DiscoverViewState.DefaultCategoryView -> {
                swipeRefreshLayout.isRefreshing = false
                categoriesRecyclerView.adapter ?: let {
                    categoriesRecyclerView.adapter = categoriesAdapter
                    categoriesRecyclerView.layoutManager = LinearLayoutManager(context,
                            LinearLayoutManager.HORIZONTAL, false)
                    categoriesRecyclerView.addItemDecoration(DividerItemDecoration(context,
                            DividerItemDecoration.HORIZONTAL))
                }
                categoriesAdapter.items = state.categories
                categoriesAdapter.selectedItem = state.categories.firstOrNull()
            }
            is DiscoverViewState.DefaultPostsView -> {
                postAdapter.isLoading = false
                swipeRefreshLayout.isRefreshing = false
                postAdapter.items = state.posts
                postAdapter.nextPageId = state.nextPageId
            }
            is DiscoverViewState.ErrorView -> {
                swipeRefreshLayout.isRefreshing = false
                context?.showError(state.errorMessage)
            }
            is DiscoverViewState.LoadingCategoriesView -> swipeRefreshLayout.isRefreshing = true
            is DiscoverViewState.LoadingPostsView -> {
                postRecyclerView?.adapter ?: let {
                    postRecyclerView.adapter = postAdapter
                    postRecyclerView.layoutManager = LinearLayoutManager(context)
                }
                postAdapter.isLoading = true
            }
        }
    }

    private fun loadCategoryIntent(): Observable<DiscoverIntent> = rxLifecycle.filter {
        it == Lifecycle.Event.ON_START
    }.map { DiscoverIntent.LoadCategories }

    private fun loadPostsIntent(): Observable<DiscoverIntent> = categoriesAdapter.onItemSelected()
            .map { categoriesAdapter.selectedItem?.let { DiscoverIntent.LoadPosts(it) } }

    private fun refreshIntent(): Observable<DiscoverIntent> = RxSwipeRefreshLayout.refreshes(swipeRefreshLayout)
            .map { categoriesAdapter.selectedItem?.let { DiscoverIntent.LoadPosts(it) } }
}
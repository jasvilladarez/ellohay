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

package io.github.jasvilladarez.ello.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

open class RecyclerAdapter<T>(
        private val defaultViewItem: RecyclerViewItem<T>,
        private val loadMoreDistance: Int = 1,
        private val loadMoreItem: RecyclerViewItem<Unit>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_ITEM_DEFAULT = 0
        private const val VIEW_ITEM_PROGRESS = 1
    }

    private var _items: MutableList<T> = mutableListOf()

    var items: List<T>
        get() = _items
        set(value) {
            _items = value.toMutableList()
            notifyDataSetChanged()
        }

    private var onLoadMore: (() -> Unit)? = null
    var isLoading: Boolean = false
        set(value) {
            field = value
            notifyItemChanged(itemCount)
        }

    private var onItemClick: ((T) -> Unit)? = null

    var selectedItem: T? = null
        set(value) {
            notifyItemChanged(items.indexOf(field))
            field = value
            onItemSelected?.invoke(value)
            notifyItemChanged(items.indexOf(field))
        }
    private var onItemSelected: ((T?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(when (viewType) {
                        VIEW_ITEM_PROGRESS -> loadMoreItem.viewItemLayout
                        else -> defaultViewItem.viewItemLayout
                    }, parent, false)) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_ITEM_PROGRESS -> loadMoreItem.bind(holder.itemView, Unit, null)
            else -> {
                defaultViewItem.bind(holder.itemView, items[position], selectedItem)
                holder.itemView.setOnClickListener {
                    selectedItem = items[position]
                    onItemClick?.invoke(items[position])
                }
                if (position == itemCount - loadMoreDistance) {
                    onLoadMore?.invoke()
                }
            }
        }
    }

    override fun getItemCount(): Int = if (isLoading) items.size + 1 else items.size

    override fun getItemViewType(position: Int): Int = if (isLoading && position == itemCount - 1) {
        VIEW_ITEM_PROGRESS
    } else {
        VIEW_ITEM_DEFAULT
    }

    fun onLoadMore(): Observable<Unit> = OnLoadMoreItems(this)

    fun onItemClick(): Observable<T> = ItemClick(this)

    fun onItemSelected(): Observable<T?> = SelectedItem(this)

    fun addItems(items: List<T>) {
        if (this.items.containsAll(items)) {
            return
        }
        val prevCount = itemCount
        _items.addAll(items)
        notifyItemRangeInserted(prevCount, itemCount)
    }

    fun addItem(item: T) {
        isLoading = false
        if (items.contains(item)) {
            return
        }
        _items.add(item)
        notifyItemInserted(itemCount)
    }

    private class OnLoadMoreItems(
            private val adapter: RecyclerAdapter<*>
    ) : Observable<Unit>() {

        override fun subscribeActual(observer: Observer<in Unit>?) {
            observer?.let {
                adapter.onLoadMore = object : Function0<Unit>, MainThreadDisposable() {

                    override fun invoke() {
                        if (!isDisposed) {
                            it.onNext(Unit)
                        }
                    }

                    override fun onDispose() {
                        adapter.onLoadMore = null
                    }
                }
            }
        }

    }

    private class ItemClick<T>(
            private val adapter: RecyclerAdapter<T>
    ) : Observable<T>() {

        override fun subscribeActual(observer: Observer<in T>?) {
            observer?.let {
                adapter.onItemClick = object : Function1<T, Unit>, MainThreadDisposable() {

                    override fun invoke(item: T) {
                        if (!isDisposed) {
                            it.onNext(item)
                        }
                    }

                    override fun onDispose() {
                        adapter.onItemClick = null
                    }
                }
            }
        }
    }

    private class SelectedItem<T>(
            private val adapter: RecyclerAdapter<T>
    ) : Observable<T?>() {

        override fun subscribeActual(observer: Observer<in T?>?) {
            observer?.let {
                adapter.onItemSelected = object : Function1<T?, Unit>, MainThreadDisposable() {

                    override fun invoke(item: T?) {
                        if (!isDisposed) {
                            it.onNext(item)
                        }
                    }

                    override fun onDispose() {
                        adapter.onItemSelected = null
                    }
                }
            }
        }
    }
}
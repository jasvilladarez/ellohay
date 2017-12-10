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
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

internal open class RecyclerAdapter<T>(
        private val viewItem: RecyclerViewItem<T>,
        private val loadMoreDistance: Int = 1
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _items: MutableList<T> = mutableListOf()

    var items: List<T>
        get() = _items
        set(value) {
            _items = value.toMutableList()
            notifyDataSetChanged()
        }

    var onLoadMore: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            object : RecyclerView.ViewHolder(View.inflate(parent.context,
                    viewItem.viewItemLayout, null)) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        viewItem.bind(holder.itemView, items[position])
        if (position == itemCount - loadMoreDistance) {
            onLoadMore?.invoke()
        }
    }

    override fun getItemCount(): Int = items.size

    fun loadMore(): Observable<Unit> = OnLoadMoreItems(this)

    fun addItems(items: List<T>) {
        if (this.items.containsAll(items)) {
            return
        }
        val prevCount = itemCount
        _items.addAll(items)
        notifyItemRangeInserted(prevCount, itemCount)
    }

    fun addItem(item: T) {
        if (items.contains(item)) {
            return
        }
        _items.add(item)
        notifyItemInserted(itemCount)
    }
}

internal class OnLoadMoreItems(
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
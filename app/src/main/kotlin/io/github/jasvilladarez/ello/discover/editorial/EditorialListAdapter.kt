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

package io.github.jasvilladarez.ello.discover.editorial

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.github.jasvilladarez.domain.entity.Editorial
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.util.loadImage
import io.github.jasvilladarez.ello.util.fromHtml
import kotlinx.android.synthetic.main.li_editorial.view.*

internal class EditorialListAdapter(
        editorialList: List<Editorial>? = null
) : RecyclerView.Adapter<EditorialListAdapter.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_POST_STREAM = 1
    }

    var list: List<Editorial>? = editorialList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? = parent?.let {
        ViewHolder(View.inflate(it.context, R.layout.li_editorial, null))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        list?.get(position)?.let {
            holder?.bind(it)
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    override fun getItemViewType(position: Int): Int = list?.getOrNull(position)?.takeIf {
        it.kind == Editorial.EditorialType.POST_STREAM
    }?.let { VIEW_TYPE_POST_STREAM } ?: VIEW_TYPE_DEFAULT

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(editorial: Editorial) {
            editorial.image?.mdpi?.url?.let {
                itemView.editorialImage.loadImage(it)
            }
            itemView.title.text = editorial.title
            itemView.description.text = editorial.renderedSubtitle?.fromHtml()
        }
    }
}
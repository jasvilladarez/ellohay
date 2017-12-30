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

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.util.ui.loadImage
import io.github.jasvilladarez.ello.util.ui.setVisible
import io.github.jasvilladarez.ello.widget.RecyclerViewItem
import kotlinx.android.synthetic.main.li_post.view.*

internal class PostViewItem : RecyclerViewItem<PostItem> {

    override val viewItemLayout: Int
        get() = R.layout.li_post

    override fun bind(view: View, item: PostItem) {
        item.authorImageUrl?.let { view.authorImage.loadImage(it) }
        val username = "@${item.authorUsername}"
        view.authorName.text = username

        view.content.removeAllViews()
        item.summary?.forEach {
            val childView = when (it) {
                is ImagePostBlockItem -> it.imageUrl?.let {
                    ImageView(view.context).apply {
                        adjustViewBounds = true
                        minimumHeight = context.resources
                                .getDimensionPixelSize(R.dimen.li_default_image_height)
                        setBackgroundColor(ContextCompat.getColor(context, R.color.ello_foreground))
                        loadImage(it)
                    }
                }
                is TextPostBlockItem -> it.text?.let {
                    TextView(view.context).apply {
                        val defaultPadding = context.resources
                                .getDimensionPixelSize(R.dimen.default_padding)
                        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding)
                        setTextColor(ContextCompat.getColor(context, R.color.ello_black))
                        setLinkTextColor(ContextCompat.getColor(context, R.color.ello_black))
                        text = it
                    }
                }
                else -> null
            }
            childView?.let { view.content.addView(it) }
        }
        view.content.setVisible(view.content.childCount > 0)
    }

}
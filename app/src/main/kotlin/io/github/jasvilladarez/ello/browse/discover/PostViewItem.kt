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

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.util.ui.loadImage
import io.github.jasvilladarez.ello.util.ui.setVisible
import io.github.jasvilladarez.ello.widget.RecyclerViewItem
import kotlinx.android.synthetic.main.li_post.view.*
import kotlin.math.roundToInt

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
                is ImagePostBlockItem -> it.toImageView(view.context)
                is TextPostBlockItem -> it.toTextView(view.context)
                else -> null
            }
            childView?.let { view.content.addView(it) }
        }
        view.content.setVisible(view.content.childCount > 0)
    }

    private fun ImagePostBlockItem.toImageView(context: Context): ImageView? = this.thumbnailUrl?.let {
        val imageRatio = this.imageRatio
        ImageView(context).apply {
            adjustViewBounds = true
            setPadding(paddingLeft, paddingTop, paddingRight, context.resources
                    .getDimensionPixelSize(R.dimen.default_half_padding))
            viewTreeObserver.addOnPreDrawListener(
                    object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            viewTreeObserver.removeOnPreDrawListener(this)
                            val width = measuredWidth
                            minimumHeight = imageRatio?.let {
                                width / it
                            }?.roundToInt() ?: context.resources
                                    .getDimensionPixelSize(R.dimen.li_default_image_height)
                            return true
                        }
                    }
            )
            loadImage(it)
        }
    }

    private fun TextPostBlockItem.toTextView(context: Context): TextView? =
            this.text?.takeIf { it.isNotEmpty() }?.let {
                TextView(context).apply {
                    val defaultPadding = context.resources
                            .getDimensionPixelSize(R.dimen.default_padding)
                    setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding)
                    setTextColor(ContextCompat.getColor(context, R.color.ello_black))
                    setLinkTextColor(ContextCompat.getColor(context, R.color.ello_black))
                    text = it
                }
            }
}
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

import android.view.View
import io.github.jasvilladarez.domain.entity.Editorial
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.util.fromHtml
import io.github.jasvilladarez.ello.util.ui.loadImage
import io.github.jasvilladarez.ello.widget.RecyclerViewItem
import kotlinx.android.synthetic.main.li_editorial.view.*

internal class EditorialViewItem : RecyclerViewItem<Editorial> {

    override val viewItemLayout: Int
        get() = R.layout.li_editorial

    override fun bind(view: View, item: Editorial) {
        item.image?.mdpi?.url?.let {
            view.editorialImage?.loadImage(it)
        }
        view.title?.text = item.title
        view.description?.text = item.renderedSubtitle?.fromHtml()
    }

}
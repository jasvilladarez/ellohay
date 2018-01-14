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

import android.support.v4.content.ContextCompat
import android.view.View
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.util.formatDate
import io.github.jasvilladarez.ello.util.fromHtml
import io.github.jasvilladarez.ello.util.getDaysRemaining
import io.github.jasvilladarez.ello.util.toDate
import io.github.jasvilladarez.ello.util.ui.loadImage
import io.github.jasvilladarez.ello.util.ui.setVisible
import io.github.jasvilladarez.ello.widget.RecyclerViewItem
import kotlinx.android.synthetic.main.li_artist_invite.view.*

internal class ArtistInviteViewItem : RecyclerViewItem<ArtistInviteItem> {

    override val viewItemLayout: Int
        get() = R.layout.li_artist_invite

    override fun bind(view: View, item: ArtistInviteItem) {
        item.headerImageUrl?.let {
            view.artistInviteHeaderImage?.loadImage(it)
        }
        item.logoImageUrl?.let {
            view.artistInviteLogoImage?.loadImage(it)
        }
        view.title?.text = item.title
        view.inviteType?.text = item.inviteType
        view.description?.text = item.description?.fromHtml()
        setStatusText(view, item.status)
        setTime(view, item)
    }

    private fun setStatusText(view: View, status: ArtistInviteItem.Status) = when (status) {
        ArtistInviteItem.Status.UPCOMING -> {
            view.status?.text = view.context.getString(R.string.upcoming)
            view.status?.setTextColor(ContextCompat.getColor(view.context, R.color.ello_purple))
        }
        ArtistInviteItem.Status.OPEN -> {
            view.status?.text = view.context.getString(R.string.open_for_submissions)
            view.status?.setTextColor(ContextCompat.getColor(view.context, R.color.ello_green))
        }
        ArtistInviteItem.Status.SELECTING -> {
            view.status?.text = view.context.getString(R.string.selection_in_progress)
            view.status?.setTextColor(ContextCompat.getColor(view.context, R.color.ello_orange))
        }
        else -> {
            view.status?.text = view.context.getString(R.string.invite_closed)
            view.status?.setTextColor(ContextCompat.getColor(view.context, R.color.ello_red))
        }
    }

    private fun setTime(view: View, artistInvite: ArtistInviteItem) {
        view.time?.setVisible(true)
        when (artistInvite.status) {
            ArtistInviteItem.Status.UPCOMING -> {
                val opensDay = "${view.context.getString(R.string.opens)} " +
                        artistInvite.openedAt.toDate().formatDate("MMMM dd, yyyy")
                view.time?.text = opensDay
            }
            ArtistInviteItem.Status.OPEN -> {
                val daysRemaining = "${artistInvite.closedAt.toDate().getDaysRemaining()} " +
                        view.context.getString(R.string.days_remaining)
                view.time?.text = daysRemaining
            }
            ArtistInviteItem.Status.SELECTING ->
                view.time?.text = view.context.getString(R.string.hold_tight)
            ArtistInviteItem.Status.CLOSED ->
                view.time?.text = artistInvite.openedAt.toDate().formatDate("MMMM yyyy")
            else -> view.time?.setVisible(false)
        }
    }
}
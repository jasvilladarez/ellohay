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

import android.view.View
import io.github.jasvilladarez.domain.entity.ArtistInvite
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.util.getDaysRemaining
import io.github.jasvilladarez.ello.util.formatDate
import io.github.jasvilladarez.ello.util.fromHtml
import io.github.jasvilladarez.ello.util.ui.loadImage
import io.github.jasvilladarez.ello.util.ui.setVisible
import io.github.jasvilladarez.ello.widget.RecyclerViewItem
import kotlinx.android.synthetic.main.li_artist_invite.view.*

internal class ArtistInviteViewItem : RecyclerViewItem<ArtistInvite> {

    override val viewItemLayout: Int
        get() = R.layout.li_artist_invite

    override fun bind(view: View, item: ArtistInvite) {
        item.headerImage.mdpi?.url?.let {
            view.artistInviteHeaderImage?.loadImage(it)
        }
        item.logoImage.optimized?.url?.let {
            view.artistInviteLogoImage?.loadImage(it)
        }
        view.title?.text = item.title
        view.inviteType?.text = item.inviteType
        view.description?.text = item.shortDescription.fromHtml()
        setStatusText(view, item.status)
        setTime(view, item)
    }

    private fun setStatusText(view: View, status: ArtistInvite.Status) = when (status) {
        ArtistInvite.Status.UPCOMING -> {
            view.status?.text = view.context.getString(R.string.upcoming)
            view.status?.setTextColor(view.context.resources.getColor(R.color.ello_purple))
        }
        ArtistInvite.Status.OPEN -> {
            view.status?.text = view.context.getString(R.string.open_for_submissions)
            view.status?.setTextColor(view.context.resources.getColor(R.color.ello_green))
        }
        ArtistInvite.Status.SELECTING -> {
            view.status?.text = view.context.getString(R.string.selection_in_progress)
            view.status?.setTextColor(view.context.resources.getColor(R.color.ello_orange))
        }
        else -> {
            view.status?.text = view.context.getString(R.string.invite_closed)
            view.status?.setTextColor(view.context.resources.getColor(R.color.ello_red))
        }
    }

    private fun setTime(view: View, artistInvite: ArtistInvite) {
        view.time?.setVisible(true)
        when (artistInvite.status) {
            ArtistInvite.Status.OPEN -> {
                val daysRemaining = "${artistInvite.closedAt.getDaysRemaining()} " +
                        view.context.getString(R.string.days_remaining)
                view.time?.text = daysRemaining
            }
            ArtistInvite.Status.SELECTING ->
                view.time?.text = view.context.getString(R.string.hold_tight)
            ArtistInvite.Status.CLOSED ->
                view.time?.text = artistInvite.openedAt.formatDate("MMMM yyyy")
            else -> view.time?.setVisible(false)
        }
    }
}
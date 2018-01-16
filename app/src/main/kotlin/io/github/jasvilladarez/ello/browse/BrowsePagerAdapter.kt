/*
 * MIT License
 *
 * Copyright (c) 2018 Jasmine Villadarez
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

package io.github.jasvilladarez.ello.browse

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import io.github.jasvilladarez.ello.R
import io.github.jasvilladarez.ello.browse.discover.DiscoverFragment
import io.github.jasvilladarez.ello.browse.editorial.EditorialFragment
import io.github.jasvilladarez.ello.browse.invites.ArtistInvitesFragment

internal class BrowsePagerAdapter(
        private val context: Context,
        fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {

    companion object {
        private const val BROWSE_PAGE_COUNT = 3
        private const val PAGE_EDITORIAL = 0
        private const val PAGE_INVITES = 1
        private const val PAGE_DISCOVER = 2
    }

    override fun getCount(): Int = BROWSE_PAGE_COUNT

    override fun getItem(position: Int): Fragment = when (position) {
        PAGE_EDITORIAL -> EditorialFragment()
        PAGE_INVITES -> ArtistInvitesFragment()
        PAGE_DISCOVER -> DiscoverFragment()
        else -> throw IllegalStateException("No fragment available for this position $position")
    }

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        PAGE_EDITORIAL -> context.getString(R.string.editorial)
        PAGE_INVITES -> context.getString(R.string.artist_invites)
        PAGE_DISCOVER -> context.getString(R.string.discover)
        else -> throw IllegalStateException("No fragment available for this position $position")
    }
}
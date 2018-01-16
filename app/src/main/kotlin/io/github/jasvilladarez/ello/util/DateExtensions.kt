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

package io.github.jasvilladarez.ello.util

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

private val ELLO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

internal fun String.toDate(): Date {
    val dateFormat = SimpleDateFormat(ELLO_DATE_FORMAT, Locale.getDefault())
    return dateFormat.parse(this)
}

internal fun Date.formatDate(format: String): String {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(this)
}

internal fun Date.getDaysRemaining(): Int =
        ((this.time - System.currentTimeMillis()) / DateUtils.DAY_IN_MILLIS).toInt()
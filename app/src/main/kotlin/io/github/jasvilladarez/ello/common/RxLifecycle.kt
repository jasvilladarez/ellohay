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

package io.github.jasvilladarez.ello.common

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.Observable
import io.reactivex.Observer

internal class RxLifecycle : LifecycleObserver, Observable<Lifecycle.Event>() {

    private var observer: Observer<in Lifecycle.Event>? = null

    override fun subscribeActual(observer: Observer<in Lifecycle.Event>) {
        this.observer = observer
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        observer?.onNext(Lifecycle.Event.ON_CREATE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        observer?.onNext(Lifecycle.Event.ON_START)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        observer?.onNext(Lifecycle.Event.ON_RESUME)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        observer?.onNext(Lifecycle.Event.ON_PAUSE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        observer?.onNext(Lifecycle.Event.ON_STOP)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        observer?.onNext(Lifecycle.Event.ON_DESTROY)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny() {
        observer?.onNext(Lifecycle.Event.ON_ANY)
    }

}
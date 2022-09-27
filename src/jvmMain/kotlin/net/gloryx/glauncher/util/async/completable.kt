package net.gloryx.glauncher.util.async

import cat.try_

interface LazyDeferred<T> {
    val maybe: T?
    suspend fun await(): T

    suspend fun tryAwait(): T? = try_ { await() }
}
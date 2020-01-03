package com.ghostwan.sample.geofencing.utils

inline fun <T, R> T.ifNotNull(block: (T) -> R): R = this.let(block)
inline fun <T, R> T.elseNull(block: T.() -> R): R = this.run(block)

package com.yunze.rxjavasample.utils

/** Apply a block of code to an object only if a condition is met. */
inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    return if (condition) {
        this.apply(block)
    } else {
        this
    }
}

/** Checking if a collection is not empty. */
fun <T> Collection<T>?.notEmpty(): Boolean {
    return this?.isNotEmpty() == true
}

/** Execute the block only if the value is not null. */
inline fun <T : Any, R> T?.withNotNull(block: (T) -> R): R? {
    return this?.let(block)
}
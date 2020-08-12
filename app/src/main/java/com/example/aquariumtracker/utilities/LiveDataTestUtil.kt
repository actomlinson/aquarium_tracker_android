package com.example.aquariumtracker.utilities

import android.annotation.SuppressLint
import androidx.annotation.Nullable
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * From https://github.com/android/architecture-components-samples/blob/17c315a050745c61ae8e79000bc0251305bd148b/BasicSample/app/src/androidTest/java/com/example/android/persistence/LiveDataTestUtil.java
 * and here:
 * https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/room/integration-tests/kotlintestapp/src/androidTest/java/androidx/room/integration/kotlintestapp/test/LiveDataTestUtil.kt
 * */



/**
 * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds.
 * Once we got a notification via onChanged, we stop observing.
 */
@SuppressLint("RestrictedApi")
@Throws(InterruptedException::class)
fun <T> getValue(liveData: LiveData<T>): T? {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer: Observer<T> = object : Observer<T> {
        override fun onChanged(@Nullable o: T) {
            data[0] = o
            latch.countDown()
            liveData.removeObserver(this)
        }
    }
    ArchTaskExecutor.getMainThreadExecutor().execute {
        liveData.observeForever(observer)

    }
    latch.await(2, TimeUnit.SECONDS)

    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}

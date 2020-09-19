package com.example.aquariumtracker

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class WorkManagerTest {
    lateinit var context: Context

    @Before
    fun setup() {

        context = InstrumentationRegistry.getInstrumentation().targetContext
        Log.i("before", context.toString())
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    @Throws(Exception::class)
    fun testSimple() {
        // Create request
        val request = OneTimeWorkRequestBuilder<NotifyWorker>()
            .build()

        val workManager = WorkManager.getInstance(context)
        // Enqueue and wait for result. This also runs the Worker synchronously
        // because we are using a SynchronousExecutor.
        workManager.enqueue(request).result.get()
        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()
        val outputData = workInfo.outputData
        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }

    @Test
    @Throws(Exception::class)
    fun testWithInitialDelay() {
        val request = OneTimeWorkRequestBuilder<NotifyWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        val workManager = WorkManager.getInstance(getApplicationContext())
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)

        workManager.enqueue(request).result.get()
        var workInfo = workManager.getWorkInfoById(request.id).get()
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))

        testDriver?.setInitialDelayMet(request.id)
        workInfo = workManager.getWorkInfoById(request.id).get()
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }

    @Test
    @Throws(Exception::class)
    fun testPeriodicWork() {
        // Create request
        val request = PeriodicWorkRequestBuilder<NotifyWorker>(15, TimeUnit.MINUTES)
            .build()

        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        // Enqueue and wait for result.

        workManager.enqueue(request).result.get()
        val workInfo = workManager.getWorkInfoById(request.id).get()
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))

        // Tells the testing framework the period delay is met
        testDriver?.setPeriodDelayMet(request.id)
        // Get WorkInfo and outputData
        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }

}
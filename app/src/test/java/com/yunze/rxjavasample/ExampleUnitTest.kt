package com.yunze.rxjavasample

import com.jakewharton.rx3.replayingShare
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subscribers.TestSubscriber
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val dataSource1 = Flowable.fromIterable(listOf(1,2,3,4,5))

    private var counter = 0
    private val dataSource2 = Flowable.defer {
        Flowable.fromCallable {
            println("a complicated operation is started!")
            Thread.sleep(1000)
            counter += 1
            println("counter updated to $counter")
            counter
        }.replayingShare()
    }

    private val dataSource3 = Flowable.defer {
        Flowable.intervalRange(1, 10, 0L, 1000L, TimeUnit.MILLISECONDS)
    }
    private val dataSource4 = Flowable.intervalRange(1, 10, 0L, 1000L, TimeUnit.MILLISECONDS)

    @Test
    fun `test1`() {
        val testSubscriber = dataSource1.test()

        testSubscriber.assertValueSequence(listOf(1,2,3,4,5))
    }

    @Test
    fun `test2`() {
        val testSubscriber1 = TestSubscriber<Int>()
        dataSource2.subscribe(testSubscriber1)

        testSubscriber1.assertValue(1)
    }

    @Test
    fun `test3`() {
        val testSubscriber1 = object : TestSubscriber<Long>() {
            override fun onNext(t: Long) {
                println("${System.currentTimeMillis()}: testSubscriber1 data received $t")
                super.onNext(t)
            }
        }

        val testSubscriber2 = object : TestSubscriber<Long>() {
            override fun onNext(t: Long) {
                println("${System.currentTimeMillis()}: testSubscriber2 data received $t")
                super.onNext(t)
            }
        }

        val flowable = Flowable.defer {
            Flowable.intervalRange(1, 10, 0L, 1000L, TimeUnit.MILLISECONDS)
                .doOnNext {
                    println("${System.currentTimeMillis()}: data source emitted $it")
                }
        }.replayingShare()

        flowable.subscribe(testSubscriber1)
        flowable.delaySubscription(5L, TimeUnit.SECONDS).subscribe(testSubscriber2)
        testSubscriber1.awaitDone(15L, TimeUnit.SECONDS)
        testSubscriber1.assertValueSequence(LongRange(1L, 10L))
        testSubscriber2.assertValueSequence(LongRange(6L, 10L))
    }
}
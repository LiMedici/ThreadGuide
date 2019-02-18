package chapter1.lesson9

import java.util.*
import java.util.concurrent.TimeUnit



private class UnSafeTask : Runnable{

    private lateinit var startDate:Date

    override fun run() {
        startDate = Date()
        println("Starting Thread: ${Thread.currentThread().id} : $startDate")
        try {
            TimeUnit.SECONDS.sleep(Math.rint(Math.random() * 10).toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        println("Thread Finished: ${Thread.currentThread().id} : $startDate")
    }
}

private class SafeTask : Runnable{

    private val threadLocal = object: ThreadLocal<Date>(){
        override fun initialValue() : Date{
            return Date()
        }
    }

    override fun run() {
        println("Starting Thread: ${Thread.currentThread().id} : ${threadLocal.get()}")
        try {
            TimeUnit.SECONDS.sleep(Math.rint(Math.random() * 10).toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        println("Thread Finished: ${Thread.currentThread().id} : ${threadLocal.get()}")
    }
}


fun main(args: Array<String>) {
    // val task = UnSafeTask()
    val task = SafeTask()
    for (i in 0..1) {
        val thread = Thread(task)
        thread.start()
        try {
            TimeUnit.SECONDS.sleep(2)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
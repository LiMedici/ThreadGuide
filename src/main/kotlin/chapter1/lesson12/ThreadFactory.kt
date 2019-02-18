package chapter1.lesson12

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class ThreadFactory

private class DefaultThreadFactory: ThreadFactory {

    private val poolNumber = AtomicInteger(1)
    private var group:ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private var namePrefix:String

    init {
        val manager = System.getSecurityManager()
        group = if(manager != null){
            manager.threadGroup
        }else{
            Thread.currentThread().threadGroup
        }

        namePrefix = "pool-${poolNumber.getAndIncrement()}-thread-"
    }

    override fun newThread(runnable : Runnable?): Thread {
        val thread = Thread(group, runnable,namePrefix + threadNumber.getAndIncrement(),0)
        if (thread.isDaemon) thread.isDaemon = false
        if (thread.priority != Thread.NORM_PRIORITY) thread.priority = Thread.NORM_PRIORITY
        return thread
    }
}

fun main(args: Array<String>) {
    val executorService = Executors.newSingleThreadExecutor(DefaultThreadFactory())
    executorService.execute{
        println("Thread Pool Running")
    }
}
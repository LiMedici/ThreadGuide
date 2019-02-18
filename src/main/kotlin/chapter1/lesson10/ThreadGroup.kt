package chapter1.lesson10

import java.lang.ThreadGroup
import java.util.concurrent.TimeUnit
import java.util.*
import java.lang.Thread.activeCount
import java.lang.Thread.enumerate
import java.lang.Thread.activeCount






class ThreadGroup

private data class Result constructor(var name:String?){
    constructor():this(null)
}

private class SearchTask constructor(val result:Result) : Runnable{
    override fun run() {
        val name = Thread.currentThread().name
        println("Thread $name Started")
        try{
            doTask()
            result.name = name
        }catch (ignore:InterruptedException){
            println("Thread $name Interrupted")
            return
        }

        println("Thread $name Finished.")
    }

    private fun doTask(){
        val random = Random(Date().time)
        val value = (random.nextDouble() * 100).toInt()
        System.out.printf("Thread ${Thread.currentThread().name}: $value")
        TimeUnit.SECONDS.sleep(value.toLong())
    }
}

fun main(args: Array<String>) {
    val threadGroup = ThreadGroup("Searcher")
    val result = Result()
    val searchTask = SearchTask(result)

    for(index in 0 until 5){
        val thread = Thread(threadGroup,searchTask)
        thread.start()

        TimeUnit.SECONDS.sleep(1)
    }

    println("Number of Threads ${threadGroup.activeCount()}")
    println("Information about the Thread Group")
    threadGroup.list()

    val threads = arrayOfNulls<Thread>(threadGroup.activeCount())
    threadGroup.enumerate(threads)
    threads.forEach { println("Thread ${it?.name}:${it?.state}") }

    waitFinish(threadGroup)
    threadGroup.interrupt()
}

private fun waitFinish(threadGroup:ThreadGroup){
    while (threadGroup.activeCount() > 9) {
        try {
            TimeUnit.SECONDS.sleep(1)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
}
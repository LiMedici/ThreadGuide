package chapter2.lesson4

import java.util.concurrent.locks.ReentrantLock

private class PrintQueue{
    private val queueLock = ReentrantLock()

    fun printJob(document:Any){
        queueLock.lock()

        try {
            val duration = (Math.random() * 10000).toLong()
            println("${Thread.currentThread().name} PrintQueue : Printing a Job during ${duration / 1000} seconds")
            Thread.sleep(duration)
        }catch (e:InterruptedException){
            e.printStackTrace()
        }finally {
            queueLock.unlock()
        }
    }
}

private class Job(private val printQueue: PrintQueue) : Runnable{
    override fun run() {
        println("${Thread.currentThread().name} : Going to print a document")
        printQueue.printJob(Any())
        println("${Thread.currentThread().name} : The document has been printed")
    }
}

fun main(args: Array<String>) {
    val printQueue = PrintQueue()
    val threads = arrayOfNulls<Thread>(10)
    for(index in 0 until threads.size){
        threads[index] = Thread(Job(printQueue),"Thread $index")
    }

    threads.forEach { it!!.start() }
}
package chapter3.lesson1

import java.util.concurrent.Semaphore
import java.util.concurrent.locks.ReentrantLock

private class PrintQueue{
    // fair 是否公平模式下运行
    private val semaphore = Semaphore(1,false)

    fun printJob(document:Any){
        semaphore.acquire()

        try {
            val duration = (Math.random() * 10000).toLong()
            println("${Thread.currentThread().name} PrintQueue : Printing a Job during ${duration / 1000} seconds")
            Thread.sleep(duration)
        }catch (e:InterruptedException){
            e.printStackTrace()
        }finally {
            semaphore.release()
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
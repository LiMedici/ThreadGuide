package chapter3.lesson2

import java.lang.Exception
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

// 在例子中的PrintQueue类的关键是：Semaphore对象创建的构造方法是使用3作为参数的。
// 这个例子中，前3个调用acquire() 方法的线程会获得临界区的访问权，其余的都会被阻塞 。
private class PrintQueue{
    // fair 是否公平模式下运行
    private val semaphore = Semaphore(3,false)
    private val freePrinters = arrayOf(true,true,true)

    private val lockPrinters = ReentrantLock()

    fun printJob(document:Any){

        try {
            semaphore.acquire()

            val assignedPrinter = getPrinter()

            val duration = (Math.random() * 10000).toLong()
            println("${Thread.currentThread().name} PrintQueue : Printing a Job in Printer$assignedPrinter during ${duration / 1000} seconds")
            TimeUnit.MILLISECONDS.sleep(duration)

            freePrinters[assignedPrinter] = true
        }catch (e:InterruptedException){
            e.printStackTrace()
        }finally {
            semaphore.release()
        }
    }

    private fun getPrinter():Int{
        var ret = -1

        try {
            lockPrinters.lock()

            for (index in 0..freePrinters.size) {
                if (freePrinters[index]) {
                    ret = index
                    freePrinters[index] = false
                    break
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            lockPrinters.unlock()
        }

        return ret


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
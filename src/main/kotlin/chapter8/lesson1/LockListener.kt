package chapter8.lesson1

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

private class MyLock : ReentrantLock() {

    fun getOwnerName(): String {
        if(this.owner == null){
            return "None"
        }

        return this.owner.name
    }

    fun getThreads():Collection<Thread>{
        return this.queuedThreads
    }
}

private class Task(private val lock:Lock):Runnable{
    override fun run() {
        for (index in 0 until 5){
            lock.lock()
            println("${Thread.currentThread().name} : Get the lock.")
            TimeUnit.MILLISECONDS.sleep(500)
            println("${Thread.currentThread().name} : Free the lock.")
            lock.unlock()
        }
    }
}


fun main(args: Array<String>) {
    val lock = MyLock()

    val threads = arrayOfNulls<Thread>(5)
    for (index in 0 until threads.size){
        val task = Task(lock)
        threads[index] = Thread(task)
        threads[index]!!.start()
    }

    for (index in 0 until 15){
        println("Main: Logging the lock.")
        println("************************")
        println("Lock: Owner : ${lock.getOwnerName()}")

        println("Lock: Queued Threads : ${lock.hasQueuedThreads()}")

        if(lock.hasQueuedThreads()){
            println("Lock : Queue Length : ${lock.queueLength}")
            println("Lock : Queued Threads :")
            val  lockThreads = lock.getThreads()
            for (thread in lockThreads){
                print("${thread.name} ")
            }
            println()
        }

        println("Lock: Fairness: ${lock.isFair}")
        println("Lock: Locked: ${lock.isLocked}")
        println("************************")

        TimeUnit.SECONDS.sleep(1)
    }
}
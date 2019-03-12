package chapter8.lesson6

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
// http://findbugs.sourceforge.net/
// http://code.google.com/p/ multithreadedtc/
private class Task(private val lock:ReentrantLock) : Runnable{
    override fun run() {
        lock.lock()
        TimeUnit.SECONDS.sleep(1)
        lock.unlock()
    }
}

fun main(args: Array<String>) {
    val lock = ReentrantLock()

    for (index in 0 until 10){
        val task = Task(lock)
        val thread = Thread(task)
        thread.run()
    }
}
package chapter1.lesson11

import java.util.*
import java.util.concurrent.ThreadFactory

class ThreadGroupHandler

private class MyThreadGroup(name:String) : ThreadGroup(name){

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        println("The Thread ${t?.id} has thrown an Exception")
        e?.printStackTrace(System.out)
        println("Terminating the rest of the Threads")
    }
}

private class Task : Runnable{
    override fun run() {
        val random = Random(Thread.currentThread().id)
        while (true) {
            val result = 1000 / (random.nextDouble() * 1000).toInt()
            System.out.printf("%s : f\n", Thread.currentThread().id, result)
            println("${Thread.currentThread().id} : $result")
            if (Thread.currentThread().isInterrupted) {
                System.out.printf("${Thread.currentThread().id} : Interrupted")
                return
            }
        }
    }
}

fun main(args: Array<String>) {
    val threadGroup = MyThreadGroup("ThreadGroup")
    for (index in 1..5) {
        val task = Task()
        val thread = Thread(threadGroup,task)
        thread.start()
    }

}

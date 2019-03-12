package chapter8.lesson3

import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private class Task(private val milliseconds:Long):Runnable{

    override fun run() {
        println("${Thread.currentThread().name}:Begin")
        TimeUnit.MILLISECONDS.sleep(milliseconds)
        println("${Thread.currentThread().name}:End")
    }
}

fun main(args: Array<String>) {
    val executor = Executors.newCachedThreadPool() as ThreadPoolExecutor

    val random = Random()
    for (index in 0 until 10){
        val task = Task(random.nextInt(10000).toLong())
        executor.submit(task)
    }

    for (index in 0 until 5){
        showLog(executor)
        TimeUnit.SECONDS.sleep(1)
    }

    executor.shutdown()

    for (index in 0 until 5){
        showLog(executor)
        TimeUnit.SECONDS.sleep(1)
    }

    executor.awaitTermination(1,TimeUnit.DAYS)

    println("Main:End of the program.")
}


private fun showLog(executor:ThreadPoolExecutor){
    println("************************")
    println("Main: Executor Log")
    println("Main: Executor : Core Pool Size:${executor.corePoolSize}")
    println("Main: Executor : Pool Size: ${executor.poolSize}")
    println("Main: Executor : Active Count: ${executor.activeCount}")
    println("Main: Executor : Task Count: ${executor.taskCount}")
    println("Main: Executor : Completed Task Count: ${executor.completedTaskCount}")
    println("Main: Executor : Shutdown:${executor.isShutdown}")
    println("Main: Executor : Terminating:${executor.isTerminating}")
    println("************************")
}
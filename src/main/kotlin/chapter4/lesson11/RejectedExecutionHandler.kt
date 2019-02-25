package chapter4.lesson11

import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private class RejectedTaskController : RejectedExecutionHandler{
    override fun rejectedExecution(r: Runnable?, executor: ThreadPoolExecutor?) {
        println("RejectedTaskController: The Task ${r?.toString()} has been rejected.")
        println("RejectedTaskController: ${executor?.toString()}")
        println("RejectedTaskController: Terminating:${executor?.isTerminating}")
        println("RejectedTaskController: Terminated:${executor?.isTerminated}")
    }
}

private class Task(private val name:String) : Runnable{
    override fun run() {
        println("Task $name : Starting")
        val duration = (Math.random() * 10).toLong()
        println("Task $name : ReportGenerator : Generating a report during $duration seconds.")
        TimeUnit.SECONDS.sleep(duration)
        println("Task $name : Ending")
    }

    override fun toString(): String {
        return name
    }
}


fun main(args: Array<String>) {
    val controller = RejectedTaskController()
    val executor = Executors.newCachedThreadPool() as ThreadPoolExecutor
    executor.rejectedExecutionHandler = controller

    println("Main:Starting.")
    for (index in 0 until 3){
        val task = Task("Task $index")
        executor.submit(task)
    }

    println("Main:Shutting down the Executor.")
    executor.shutdown()

    println("Main:Sending another Task.")
    val task = Task("RejectedTask")
    executor.submit(task)

    println("Main:End")
}
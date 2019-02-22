package chapter4.lesson8

import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private class Task : Runnable{
    override fun run() {
        while (true){
            println("Task:Test")
            Thread.sleep(100)
        }
    }
}

fun main(args: Array<String>) {
    val executor = Executors.newCachedThreadPool() as ThreadPoolExecutor
    val task = Task()
    println("Main:Executing the Task")
    val future = executor.submit(task)
    TimeUnit.SECONDS.sleep(2)
    println("Main:Canceling the Task")

    // 如果这个任务已经完成或之前的已被取消或由于其他原因不能被取消，那么这个方法将会返回false并且这个任务不会被取消。
    // 如果这个任务正在等待执行者获取执行它的线程，那么这个任务将被取消而且不会开始它的执行。如果这个任务已经正在运行，则视方法的参数情况而定。
    // cancel()方法接收一个Boolean值参数。如果参数为true并且任务正在运行，那么这个任务将被取消。
    // 如果参数为false并且任务正在运行，那么这个任务将不会被取消。
    future.cancel(true)
    println("Main:Canceled:${future.isCancelled}")
    println("Main:Done:${future.isDone}")

    executor.shutdown()

    println("Main:The executor has finished.")
}
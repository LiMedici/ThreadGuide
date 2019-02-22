package chapter4.lesson6

import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

private class Task(private val name:String) : Callable<String>{

    override fun call(): String {
        println("$name Starting at :${Date()}")
        return "Hello World"
    }
}

fun main(args: Array<String>) {
    val executor = Executors.newScheduledThreadPool(1) as ScheduledThreadPoolExecutor
    println("Main:Starting at:${Date()}")
    for (index in 0 until 5){
        val task = Task("Task $index")
        executor.schedule(task,(index+1).toLong(),TimeUnit.SECONDS)
    }

    // 调用shutdown()方法时，并且有待处理的任务正在等待它们延迟结束。
    // 默认的行为是，不管执行者是否结束这些任务都将被执行。
    // 你可以使用ScheduledThreadPoolExecutor类的setExecuteExistingDelayedTasksAfterShutdownPolicy()方法来改变这种行为。
    // 使用false，调用 shutdown()时，待处理的任务不会被执行
    executor.executeExistingDelayedTasksAfterShutdownPolicy = false
    executor.shutdown()

    executor.awaitTermination(1,TimeUnit.DAYS)

    println("Main:End at:${Date()}")
}
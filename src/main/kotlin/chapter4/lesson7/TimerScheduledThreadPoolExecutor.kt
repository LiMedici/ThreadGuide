package chapter4.lesson7

import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

private class Task(private val name:String) : Runnable{

    override fun run() {
        println("$name Starting at :${Date()}")
    }
}

fun main(args: Array<String>) {
    val executor = Executors.newScheduledThreadPool(1) as ScheduledThreadPoolExecutor
    println("Main:Starting at:${Date()}")
    val task = Task("Task")
    // scheduleWithFixedRate 任务执行结束与下次执行开始之间的一段时间。
    // scheduleAtFixedRate 第3个参数决定两个执行开始的一段时间。
    val scheduledFuture = executor.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS)

    for (index in 0 until 10){
        // getDelay()方法返回直到任务的下次执行时间。这个方法接收一个TimeUnit常量，这是你想要接收结果的时间单位
        println("Main:Delay:${scheduledFuture.getDelay(TimeUnit.MILLISECONDS)}")
        TimeUnit.MILLISECONDS.sleep(500)
    }

    // 当你使用 shutdown()方法时，你也可以通过参数配置一个SeduledThreadPoolExecutor的行为。
    // shutdown()方法默认的行为是，当你调用这个方法时，计划任务就结束。
    // 你可以使用ScheduledThreadPoolExecutor类的 setContinueExistingPeriodicTasksAfterShutdownPolicy()方法设置true值改变这个行为。
    // 在调用 shutdown()方法时，周期性任务将不会结束。
    executor.shutdown()

    TimeUnit.SECONDS.sleep(5)

    println("Main:End at:${Date()}")
}
package chapter4.lesson2

import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private class Task(private val name:String) : Runnable{

    private val initDate = Date()

    override fun run() {
        println("${Thread.currentThread().name}: Task $name: Created on $initDate")
        println("${Thread.currentThread().name}: Task $name: Started on ${Date()}")
        val duration = (Math.random() * 10).toLong()
        println("${Thread.currentThread().name}: Task $name: Doing a task during $duration seconds")
        TimeUnit.SECONDS.sleep(duration)
        println("${Thread.currentThread().name}: Task $name: Finished on ${Date()}")
    }
}


private class Server{

    private val executor: ThreadPoolExecutor?

    init{
        // 缓存线程池的缺点是，为新任务不断创建线程， 所以如果你提交过多的任务给执行者，会使系统超载。
        executor = Executors.newFixedThreadPool(5) as ThreadPoolExecutor
    }

    fun executeTask(task:Task){
        println("Server: A new task has arrived")
        executor?.execute(task)
        println("Server: Task Count: ${executor?.taskCount}")
    }

    fun endServer(){
        executor?.shutdown()
    }
}

fun main(args: Array<String>) {
    val server = Server()
    for (index in 0 until 100){
        val task = Task("Task $index")
        server.executeTask(task)
    }

    server.endServer()
}
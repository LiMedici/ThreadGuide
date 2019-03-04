package chapter7.lesson2

import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private class MyPriorityTask(private val name:String,
                             val priority:Int) : Runnable,Comparable<MyPriorityTask>{
    override fun run() {
        println("MyPriorityTask : $name priority : $priority")
        TimeUnit.SECONDS.sleep(2)
    }

    override fun compareTo(other: MyPriorityTask): Int {
        return when {
            this.priority < other.priority -> 1
            this.priority > other.priority -> -1
            else -> 0
        }
    }
}

fun main(args: Array<String>) {
    val executor = ThreadPoolExecutor(2,2,1,TimeUnit.SECONDS,PriorityBlockingQueue<Runnable>())
    for (index in 0 until 4){
        val task = MyPriorityTask("Task $index",index)
        executor.execute(task)
    }

    TimeUnit.SECONDS.sleep(1)

    for (index in 4 until 8){
        val task = MyPriorityTask("Task $index",index)
        executor.execute(task)
    }

    executor.shutdown()

    executor.awaitTermination(1,TimeUnit.DAYS)

    println("Main:End of the program.")
}
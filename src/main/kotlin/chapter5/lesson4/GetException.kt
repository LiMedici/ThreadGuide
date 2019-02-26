package chapter5.lesson4

import java.lang.RuntimeException
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.concurrent.TimeUnit

private class Task(private val array:IntArray,
                   private val start:Int,
                   private val end:Int) : RecursiveTask<Int>(){
    override fun compute(): Int {
        println("Task: Start from $start to $end")
        if(end - start < 10){
            if(3 > start && 3 < end){
                // throw RuntimeException("This Task throws an Exception: Task from $start to $end")
                completeExceptionally(RuntimeException("This Task throws an Exception: Task from $start to $end"))
            }

            TimeUnit.SECONDS.sleep(1)
        }else{
            val middle = (start + end) / 2
            val task1 = Task(array,start,middle)
            val task2 = Task(array,middle,end)
            invokeAll(task1,task2)
        }

        println("Task: End from $start to $end")
        return 0
    }
}

fun main(args: Array<String>) {
    val array = IntArray(100)
    val task = Task(array,0,100)

    val pool = ForkJoinPool()

    pool.execute(task)

    pool.shutdown()

    pool.awaitTermination(1,TimeUnit.DAYS)

    task.isCompletedNormally
    if(task.isCompletedAbnormally){
        println("Main: An Exception has curred")
        println("Main: ${task.exception}")
    }

    println("Main:Result:${task.get()}")
}
package chapter8.lesson4

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction
import java.util.concurrent.TimeUnit

private class Task(private val array:ByteArray,
                   private val start:Int,
                   private val end:Int) : RecursiveAction(){

    override fun compute() {
        if(end - start > 100){
            val middle = (start + end) / 2
            val task1 = Task(array,start,middle)
            val task2 = Task(array,middle,end)

            task1.fork()
            task2.fork()

            task1.join()
            task2.join()
        }else{
            for (index in start until end){
                array[index] ++
                TimeUnit.MILLISECONDS.sleep(5)
            }
        }
    }
}

fun main(args: Array<String>) {
    val pool = ForkJoinPool()
    val array = ByteArray(10000)

    val task = Task(array,0,array.size)
    pool.execute(task)

    while (!task.isDone){
        showLog(pool)
        TimeUnit.SECONDS.sleep(1)
    }

    pool.shutdown()

    pool.awaitTermination(1,TimeUnit.DAYS)

    showLog(pool)

    println("Main:End of the program.")
}

private fun showLog(pool: ForkJoinPool){
    println("*******************************")
    println("Main:Fork/Join Pool Log")
    println("Main:Fork/Join Pool: Parallelism:${pool.parallelism}")
    println("Main:Fork/Join Pool: Pool Size:${pool.poolSize}")
    println("Main:Fork/Join Pool: Active Thread Count:${pool.activeThreadCount}")
    println("Main:Fork/Join Pool: Running Thread Count:${pool.runningThreadCount}")
    println("Main:Fork/Join Pool: Queued Submission:${pool.queuedSubmissionCount}")
    println("Main:Fork/Join Pool: Queued Tasks:${pool.queuedTaskCount}")
    println("Main:Fork/Join Pool: Queued Submission:${pool.hasQueuedSubmissions()}")
    println("Main:Fork/Join Pool: Steal Count:${pool.stealCount}")
    println("Main:Fork/Join Pool: Terminated:${pool.isTerminated}")
    println("*******************************")
}
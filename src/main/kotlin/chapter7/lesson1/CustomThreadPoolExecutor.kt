package chapter7.lesson1

import java.util.*
import java.util.concurrent.*
import kotlin.collections.ArrayList

private class MyExecutor(
    corePoolSize: Int, maximumPoolSize: Int,
    keepAliveTime: Long, unit: TimeUnit, workQueue: BlockingQueue<Runnable>
) : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue) {
    private val startTimes:ConcurrentHashMap<String,Date> = ConcurrentHashMap()

    override fun shutdown() {
        println("MyExecutor:Going to shutdown.")
        println("MyExecutor:Executed task:$completedTaskCount")
        println("MyExecutor:Running tasks:$activeCount")
        println("MyExecutor:Pending tasks:${queue.size}")
        super.shutdown()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        println("MyExecutor:Going to shutdownNow.")
        println("MyExecutor:Executed task:$completedTaskCount")
        println("MyExecutor:Running tasks:$activeCount")
        println("MyExecutor:Pending tasks:${queue.size}")
        return super.shutdownNow()
    }

    override fun beforeExecute(t: Thread?, r: Runnable?) {
        super.beforeExecute(t, r)
        println("MyExecutor:A task is beginning:${t?.name}:${r?.hashCode()})")
        startTimes.put(r.hashCode().toString(),Date())
    }

    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
        try {
            val futureTask = r as FutureTask<*>
            println("******************************")
            println("MyExecutor:A task is finished.")
            println("MyExecutor:Results:${futureTask.get()}")

            val startDate = startTimes.remove(r?.hashCode().toString())
            val diff = Date().time - startDate!!.time
            println("MyExecutor:Duration:$diff")
            println("******************************")
        }catch (ignore:InterruptedException){

        }
    }
}

private class SleepTwoSecondsTask : Callable<String>{
    override fun call(): String {
        TimeUnit.SECONDS.sleep(2)
        return Date().toString()
    }
}

fun main(args: Array<String>) {
    val executor = MyExecutor(2,4,1000,TimeUnit.MILLISECONDS,LinkedBlockingQueue<Runnable>())

    val results = ArrayList<Future<String>>()
    for(index in 0 until 10){
        val task = SleepTwoSecondsTask()
        val result = executor.submit(task)
        results.add(result)
    }

    for (index in 0 until 5){
        val result = results[index].get()
        println("Main:Result for task $index : $result")
    }

    executor.shutdown()
    for (index in 5 until 10){
        val result = results[index].get()
        println("Main:Result for task $index : $result")
    }

    executor.awaitTermination(1,TimeUnit.DAYS)

    println("Main:End of the program")

}
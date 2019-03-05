package chapter7.lesson5

import java.util.*
import java.util.concurrent.*

private class MyScheduledTask<V>(runnable: Runnable, result: V?,
                                 private val task:RunnableScheduledFuture<V>,
                                 private val executor: ScheduledThreadPoolExecutor) : FutureTask<V>(runnable, result),


    RunnableScheduledFuture<V> {

    private var period:Long = 0
    private var startTime:Long = 0

    override fun compareTo(other: Delayed?): Int {
        return task.compareTo(other)
    }

    override fun isPeriodic(): Boolean {
        return task.isPeriodic
    }

    override fun getDelay(unit: TimeUnit?): Long {
        if(!isPeriodic){
            return task.getDelay(unit)
        }else{
            if(startTime == 0.toLong()){
                return task.getDelay(unit)
            }else{
                val now = Date()
                val delay = startTime - now.time
                return unit!!.convert(delay,TimeUnit.MILLISECONDS)
            }
        }
    }

    override fun run() {
        if(isPeriodic && !executor.isShutdown){
            val now = Date()
            startTime = now.time + period
            executor.queue.add(this)
        }

        println("Pre-MySheduledTask:${Date()}")
        println("MyScheduledTask: Is Periodic:${isPeriodic()}")
        super.runAndReset()
        println("Post-MyScheduledTask:${Date()}")
    }

    fun setPeriod(period:Long){
        this.period = period
    }

}


private class MyScheduledThreadPoolExecutor(corePoolSize:Int) : ScheduledThreadPoolExecutor(corePoolSize){

    override fun <V : Any?> decorateTask(
        runnable: Runnable?,
        task: RunnableScheduledFuture<V>?
    ): RunnableScheduledFuture<V> {
        return MyScheduledTask<V>(runnable!!, null,task!!,this)
    }

    override fun scheduleAtFixedRate(
        command: Runnable?,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit?
    ): ScheduledFuture<*> {
        val task =  super.scheduleAtFixedRate(command, initialDelay, period, unit)
        val myTask = task as MyScheduledTask<*>
        myTask.setPeriod(TimeUnit.MILLISECONDS.convert(period,unit))
        return task
    }

}

private class Task : Runnable{
    override fun run() {
        println("Task:Begin.")
        TimeUnit.SECONDS.sleep(2)
        println("Task:End.")
    }
}


fun main(args: Array<String>) {
    val executor = MyScheduledThreadPoolExecutor(2)

    var task = Task()
    println("Main:${Date()}")
    executor.schedule(task,1,TimeUnit.SECONDS)

    TimeUnit.SECONDS.sleep(3)


    task = Task()
    println("Main:${Date()}")
    executor.scheduleAtFixedRate(task,1,3,TimeUnit.SECONDS)

    TimeUnit.SECONDS.sleep(10)

    executor.shutdown()
    executor.awaitTermination(1,TimeUnit.DAYS)

    println("Main: End of the program.")


}
package chapter7.lesson6

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinWorkerThread
import java.util.concurrent.RecursiveTask
import java.util.concurrent.TimeUnit

private class MyWorkThread(pool:ForkJoinPool) : ForkJoinWorkerThread(pool){

    private val taskCounter = ThreadLocal<Int>()

    override fun onStart() {
        super.onStart()
        println("MyWorkThread $id : Initializing task counter.")
        taskCounter.set(0)
    }

    override fun onTermination(exception: Throwable?) {
        println("MyWorkerThread $id : ${taskCounter.get()}")
        super.onTermination(exception)
    }

    fun addTask(){
        var counter = taskCounter.get()
        counter ++
        taskCounter.set(counter)
    }
}

private class MyWorkerThreadFactory : ForkJoinPool.ForkJoinWorkerThreadFactory{
    override fun newThread(pool: ForkJoinPool?): ForkJoinWorkerThread {
        return MyWorkThread(pool!!)
    }
}

private class MyRecursiveTask(private val array:IntArray,
                              private val start:Int,
                              private val end:Int) : RecursiveTask<Int>(){
    override fun compute(): Int {
        val thread = Thread.currentThread() as MyWorkThread
        thread.addTask()

        return if(end - start <= 1){
            array[start]
        }else{
            val middle = (start + end) / 2
            val task1 = MyRecursiveTask(array,start,middle)
            val task2 = MyRecursiveTask(array,middle,end)

            addResults(task1,task2)
        }
    }

    private fun addResults(task1:MyRecursiveTask,task2:MyRecursiveTask):Int{
        task1.fork()
        task2.fork()

        return task1.join() + task2.join()
    }
}

fun main(args: Array<String>) {
    val factory = MyWorkerThreadFactory()
    val pool = ForkJoinPool(4,factory,null,false)

    val array = IntArray(100000)

    for (index in 0 until array.size) array[index] = 1
    val task = MyRecursiveTask(array,0,array.size)
    pool.execute(task)

    pool.shutdown()

    pool.awaitTermination(1, TimeUnit.DAYS)

    println("Main:Result:${task.get()}")
    println("Main:End of the program.")
}
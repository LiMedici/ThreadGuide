package chapter4.lesson9

import java.util.concurrent.*

private class ExecutableTask(val name:String) : Callable<String>{

    override fun call(): String {
        val duration = (Math.random() * 10).toLong()
        println("$name Waiting $duration seconds for results.")
        TimeUnit.SECONDS.sleep(duration)
        return "Hello World. I'm $name"
    }
}


private class ResultTask(callable: Callable<String>): FutureTask<String>(callable){

    private var name:String? = null

    init {
        name = (callable as ExecutableTask).name
    }

    override fun done() {
        if(isCancelled){
            println("$name Has been canceled.")
        }else{
            println("$name Has been finished.")
        }
    }
}

fun main(args: Array<String>) {
    val executors =  Executors.newCachedThreadPool() as ThreadPoolExecutor

    val resultTasks = arrayOfNulls<ResultTask>(5)
    for (index in 0 until resultTasks.size){
        val executorTask = ExecutableTask("Task $index")
        resultTasks[index] = ResultTask(executorTask)
        executors.submit(resultTasks[index])
    }

    TimeUnit.SECONDS.sleep(5)

    resultTasks.forEach { it!!.cancel(true) }

    resultTasks.forEach {
        if(!it!!.isCancelled){
            println("${it?.get()}")
        }
    }

    executors.shutdown()
}
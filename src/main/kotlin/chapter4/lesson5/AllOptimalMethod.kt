package chapter4.lesson5

import java.util.concurrent.*

private data class Result(var name:String?,var value:Int){

    constructor() : this(null,0)

}

private class Task(private val name:String) : Callable<Result>{
    override fun call(): Result {
        println("$name Staring")

        val duration = (Math.random() * 10).toLong()
        println("$name Waiting $duration seconds for results.")
        TimeUnit.SECONDS.sleep(duration)

        var value = 0
        for(index in 0 until 5){
            value += (Math.random() * 100).toInt()
        }

        val result = Result()
        result.name = name
        result.value = value

        println("$name : Ends")

        return result
    }
}


fun main(args: Array<String>) {
    val executor = Executors.newCachedThreadPool() as ThreadPoolExecutor

    val taskList = ArrayList<Task>()
    for(index in 0 until 3){
        val task = Task(index.toString())
        taskList.add(task)
    }

    val result = try{
        var result = executor.invokeAll(taskList)
        result
    }catch (e:InterruptedException){
        e.printStackTrace()
    }

    executor.shutdown()

    println("Main:Printing the results")
    val handler = result as  List<Future<Result>>
    handler.forEach {
        val result = it.get()
        println("${result.name} : ${result.value}")
    }
}
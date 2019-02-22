package chapter4.lesson3

import java.lang.Exception
import java.util.*
import java.util.concurrent.*

private class FactorialCalculator(private val number:Int) : Callable<Int>{

    @Throws(Exception::class)
    override fun call(): Int {
        var result = 1
        if(number == 0 || number == 1){
            result = 1
        }

        for(index in 2..number){
            result *= index
            TimeUnit.MILLISECONDS.sleep(20)
        }

        println("${Thread.currentThread().name} : $result")


        return result
    }
}


fun main(args: Array<String>) {
    val executor = Executors.newFixedThreadPool(2) as ThreadPoolExecutor

    val futureArray = ArrayList<Future<Int>>()

    val random = Random()

    for(index in 0 until 10){
        val number = random.nextInt(10)
        val calculator = FactorialCalculator(number)
        val future = executor.submit(calculator)
        futureArray.add(future)
    }

    do{
        println("Main:Number of Completed Tasks: ${executor.completedTaskCount}")
        for (index in 0 until futureArray.size){
            println("Main:Task $index : ${futureArray[index].isDone}")
        }

        TimeUnit.MILLISECONDS.sleep(50)
    }while (executor.completedTaskCount < futureArray.size)

    println("Main:Results")

    for (index in 0 until futureArray.size){
        // 当你调用Future对象的get()方法，并且这个对象控制的任务未完成，这个方法会阻塞直到任务完成。
        println("Main:Task $index : ${futureArray[index].get()}")
    }

    executor.shutdown()
}
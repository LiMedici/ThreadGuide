package chapter7.lesson7

import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.TimeUnit

private class DefaultTask(private val array: IntArray) : Runnable{
    override fun run() {
        for (index in 0 until array.size){
            if(index % 100 == 0){
                TimeUnit.MILLISECONDS.sleep(50)
            }

            array[index] ++
        }
    }
}

private abstract class MyWorkerTask(val name: String) : ForkJoinTask<Void>() {
    override fun getRawResult(): Void? {
        return null
    }

    override fun setRawResult(value: Void?) {

    }

    override fun exec(): Boolean {
        val startDate = Date()
        compute()
        val finishDate = Date()
        val diff = finishDate.time - startDate.time
        println("MyWorkerTask: $name:$diff Milliseconds to complete.")
        return true
    }

    protected abstract fun compute()

}

private class Task(
    name: String,
    private val array: IntArray,
    private val start: Int,
    private val end: Int
) : MyWorkerTask(name) {
    override fun compute() {
        if (end - start > 100) {
            val middle = (start + end) / 2
            val task1 = Task("${name}1", array, start, middle)
            val task2 = Task("${name}1", array, middle, end)

            invokeAll(task1, task2)
        } else {
            for (index in start until end) {
                array[index]++
            }
        }

        TimeUnit.MILLISECONDS.sleep(50)
    }
}

fun main(args: Array<String>) {
    val array = IntArray(10000)
    val pool = ForkJoinPool()
    val task = Task("Task", array, 0, array.size)

    pool.invoke(task)

    pool.shutdown()

    println("Main:End of the program.")

    val startDate = Date()
    val thread = Thread(DefaultTask(array))
    thread.start()
    thread.join()

    println("Test:Running Time:${Date().time - startDate.time}")


}
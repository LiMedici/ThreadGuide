package chapter7.lesson3

import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private class MyThread(target:Runnable,name:String) : Thread(target,name){

    init {
        setCreationDate()
    }

    private var creationDate: Date? = null
    private var startDate:Date? = null
    private var finishDate:Date? = null

    override fun run() {
        setStartDate()
        super.run()
        setFinishDate()

    }

    private fun setCreationDate(){
        creationDate = Date()
    }

    private fun setStartDate(){
        startDate = Date()
    }

    private fun setFinishDate(){
        finishDate = Date()
    }

    fun getExecutionTime():Long{
        return finishDate!!.time - startDate!!.time
    }

    override fun toString(): String {
        val buffer = StringBuilder()
        buffer.append(name)
        buffer.append(": ")
        buffer.append(" Creation Date: ")
        buffer.append(creationDate)
        buffer.append(" : Running time: ")
        buffer.append(getExecutionTime())
        buffer.append(" MilliseSeconds.")
        return buffer.toString()
    }


}

private class MyThreadFactory(private val prefix:String) : ThreadFactory{

    private var counter = AtomicInteger(1)

    override fun newThread(r: Runnable?): Thread {
        return MyThread(r!!,"$prefix-${counter.getAndIncrement()}")
    }
}

private class MyTask : Runnable{
    override fun run() {
        TimeUnit.SECONDS.sleep(2)
    }
}

fun main(args: Array<String>) {
    val factory = MyThreadFactory("MyThreadFactory")
    val task = MyTask()

    val thread = factory.newThread(task)

    thread.start()
    thread.join()

    println("Main: Thread information.")
    println(thread)
    println("Main:End of the example.")
}
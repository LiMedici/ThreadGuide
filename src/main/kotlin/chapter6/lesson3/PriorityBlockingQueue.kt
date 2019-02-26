package chapter6.lesson3

import java.util.concurrent.PriorityBlockingQueue

// clear()：这个方法删除队列中的所有元素。
// take()：这个方法返回并删除队列中的第一个元素。
// 如果队列是空的，这个方法将阻塞线程直到队列有元素。
// put(E e)：E是用来参数化PriorityBlockingQueue类的类。
// 这个方法将作为参数传入的元素插入到队列中。
// peek()：这个方法返回列队的第一个元素，但不删除它。

private  class Event(val thread:Int,
                     val priority:Int) : Comparable<Event>{
    override fun compareTo(other: Event): Int {
        return when {
            this.priority > other.priority -> -1
            this.priority < other.priority -> 1
            else -> 0
        }
    }
}

private class Task(private val id:Int,
                   private val queue:PriorityBlockingQueue<Event>) : Runnable{
    override fun run() {
        for(index in 0 until 1000){
            val event = Event(id,index)
            queue.put(event)
        }
    }
}

fun main(args: Array<String>) {
    val queue = PriorityBlockingQueue<Event>()
    val threads = arrayOfNulls<Thread>(5)
    for (index in 0 until threads.size){
        val task = Task(index,queue)
        threads[index] = Thread(task)
    }

    threads.forEach { it?.start() }

    threads.forEach { it?.join() }

    println("Main:Queue Size:${queue.size}")

    for(index in 0 until threads.size * 1000){
        val event = queue.poll()
        println("Thread ${event.thread} : Priority ${event.priority}")
    }

    println("Main:Queue Size:${queue.size}")
    println("Main:End of the program")
}
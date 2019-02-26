package chapter6.lesson4

import java.lang.Exception
import java.util.*
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

// clear()：这个方法删除队列中的所有元素。
// offer(E e)：E是代表用来参数化DelayQueue类的类。这个方法插入作为参数传入的元素到队列中。
// peek()：这个方法检索，但不删除队列的第一个元素。
// take()：这具方法检索并删除队列的第一个元素。
// 如果队列中没有任何激活的元素，执行这个方法的线程将被阻塞，直到队列有一些激活的元素

private class Event(private val startDate: Date) : Delayed {

    override fun compareTo(other: Delayed?): Int {
        return when {
            other == null -> 1
            this.getDelay(TimeUnit.NANOSECONDS) > other.getDelay(TimeUnit.NANOSECONDS) -> 1
            this.getDelay(TimeUnit.NANOSECONDS) < other.getDelay(TimeUnit.NANOSECONDS) -> -1
            else -> 0
        }
    }

    override fun getDelay(unit: TimeUnit?): Long {
        val now = Date()
        val diff = startDate.time - now.time
        unit?.convert(diff, TimeUnit.MILLISECONDS)
        return 0
    }
}

private class Task(
    private val id: Int,
    private val queue: DelayQueue<Event>
) : Runnable {
    override fun run() {
        val now = Date()
        val delay = Date()
        delay.time = now.time + id * 1000
        println("Thread $id:$delay")

        for (index in 0 until 100) {
            val event = Event(delay)
            queue.add(event)
        }
    }
}

fun main(args: Array<String>) {
    val queue = DelayQueue<Event>()
    val threads = arrayOfNulls<Thread>(5)
    for (index in 0 until threads.size) {
        val task = Task(index + 1, queue)
        threads[index] = Thread(task)
    }

    threads.forEach { it?.start() }

    threads.forEach { it?.join() }
    try {
        do {
            var counter = 0
            var event: Event?
            do {
                event = queue.poll()
                if (event != null) counter++
            } while (event != null)
            println("At ${Date()} you have read $counter events")
            TimeUnit.MILLISECONDS.sleep(500)
        } while (queue.size > 0)
    }catch (e:Exception){
        e.printStackTrace()
    }
}
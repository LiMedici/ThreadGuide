package chapter6.lesson1

import java.util.concurrent.ConcurrentLinkedDeque

// getFirst()和getLast()：这些方法将分别返回列表的第一个和最后一个元素。
// 它们不会从列表删除返回的元素。如果列表为空，这些方法将抛出NoSuchElementException异常。
// peek()、peekFirst()和peekLast()：这些方法将分别返回列表的第一个和最后一个元素。
// 它们不会从列表删除返回的元素。如果列表为空，这些方法将返回null值。
// remove()、removeFirst()、 removeLast()：这些方法将分别返回列表的第一个和最后一个元素。
// 它们将从列表删除返回的元素。如果列表为空，这些方法将抛出NoSuchElementException异常。

private class AddTask(private val deque: ConcurrentLinkedDeque<String>) : Runnable{

    override fun run() {
        val name = Thread.currentThread().name
        for (index in 0 until 10000){
            deque.add("$name : Element $index")
        }
    }
}

private class PollTask(private val deque:ConcurrentLinkedDeque<String>) : Runnable{
    override fun run() {
        for(index in 0 until 5000){
            deque.pollFirst()
            deque.pollLast()
        }
    }
}

fun main(args: Array<String>) {
    val deque = ConcurrentLinkedDeque<String>()
    val threads = arrayOfNulls<Thread>(100)
    for(index in 0 until threads.size){
        val addTask = AddTask(deque)
        threads[index] = Thread(addTask)
        threads[index]?.start()
    }

    println("Main:${threads.size} AddTask threads have been launched")

    threads.forEach { it?.join() }

    println("Main:Size of the List:${deque.size}")

    for(index in 0 until threads.size){
        val pollTask = PollTask(deque)
        threads[index] = Thread(pollTask)
        threads[index]?.start()
    }

    println("Main:${threads.size} PollTask threads have been launched")

    threads.forEach { it?.join() }

    println("Main:Size of the List:${deque.size}")
}
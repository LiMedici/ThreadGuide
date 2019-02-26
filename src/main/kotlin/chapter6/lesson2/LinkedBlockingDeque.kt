package chapter6.lesson2

import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

// takeFirst() 和takeLast()：这些方法分别返回列表的第一个和最后一个元素。它们从列表删除返回的元素。
// 如果列表为空，这些方法将阻塞线程，直到列表有元素。
// getFirst() 和getLast()：这些方法分别返回列表的第一个和最后一个元素。它们不会从列表删除返回的元素。
// 如果列表为空，这些方法将抛出NoSuchElementException异常。
// peek()、peekFirst(),和peekLast()：这些方法分别返回列表的第一个和最后一个元素。
// 它们不会从列表删除返回的元素。如果列表为空，这些方法将返回null值。
// poll()、pollFirst()和 pollLast()：这些方法分别返回列表的第一个和最后一个元素。
// 它们从列表删除返回的元素。如果列表为空，这些方法将返回null值。
// add()、 addFirst()、addLast()：这些方法分别在第一个位置和最后一个位置上添加元素。
// 如果列表已满（你已使用固定大小创建它），这些方法将抛出IllegalStateException异常。

private class Client(private val deque:LinkedBlockingDeque<String>) : Runnable{
    override fun run() {
        for (i in 0 until 3){
            for (j in 0 until 5){
                val request = StringBuilder()
                request.append("$i:$j")
                deque.put(request.toString())

                println("Client: $request at ${Date()}")
                TimeUnit.SECONDS.sleep(1)
            }
        }
    }
}

fun main(args: Array<String>) {
    val deque = LinkedBlockingDeque<String>(3)

    val client = Client(deque)
    val thread = Thread(client)
    thread.start()

    for (i in 0 until 5){
        for(j in 0 until 3){
            val request = deque.take()
            println("Main: Request :$request at ${Date()} Size:${deque.size}")

            TimeUnit.MILLISECONDS.sleep(300)
        }
    }

    println("Main: End of the program.")
}
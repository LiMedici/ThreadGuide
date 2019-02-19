package chapter2.lesson3

import extentions.notifyAllK
import extentions.waitK
import java.util.*

// 在同步代码中使用条件

private class EventStorage{

    private val maxSize:Int = 10
    private val storage = LinkedList<Date>()

    @Synchronized
    fun set(){
        while(storage.size >= maxSize) {
            try {
                waitK()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        storage.offer(Date())

        println("Set: ${storage.size}")
        notifyAllK()
    }

    @Synchronized
    fun get(){
        while(storage.size == 0){
            try{
                waitK()
            }catch (e:InterruptedException){
                e.printStackTrace()
            }
        }

        println("Get: ${storage.size} : ${storage.poll()}")
        notifyAllK()
    }
}

private class Producer constructor(private var storage:EventStorage) : Runnable{
    override fun run() {
        for(index in 1 until 100){
            storage.set()
        }
    }
}

private class Consumer constructor(private var storage:EventStorage) : Runnable{
    override fun run() {
        for(index in 1 until 100){
            storage.get()
        }
    }
}


fun main(args: Array<String>) {
    val storage = EventStorage()
    val producer = Producer(storage)
    val producerThread = Thread(producer)
    val consumer = Consumer(storage)
    val consumerThread = Thread(consumer)

    producerThread.start()
    consumerThread.start()
}
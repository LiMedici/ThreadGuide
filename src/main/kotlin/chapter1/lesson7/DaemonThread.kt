package chapter1.lesson7

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator
import java.util.*
import java.util.concurrent.*

// 最典型的这种类型代表就是垃圾回收器。
class DaemonThread

data class Event(var date:Date,var event:String)

private class WriteTask(private val deque:LinkedBlockingDeque<Event>) : Runnable{
    override fun run() {
        for(index in 1 .. 100){
            val event = Event(Date(),"The thread ${Thread.currentThread().id} has generated an event")
            // 不阻塞的方法
            deque.putFirst(event)
            // 线程睡眠1秒
            TimeUnit.SECONDS.sleep(1)
        }
    }
}

private class CleanerTask constructor(private val deque:LinkedBlockingDeque<Event>) : Thread(){
    init {
        isDaemon = true
    }

    override fun run() {
        while (true){
            val date = Date()
            clean(date)
        }
    }

    private fun clean(date:Date){
        if(deque.isEmpty()) return

        var delete = false
        do{
            val event = deque.last
            val difference = date.time - event.date.time
            if(difference > TimeUnit.SECONDS.toMillis(10)){
                println("Cleaner:${event.event}")
                deque.removeLast()
                delete = true
            }
        }while (difference > TimeUnit.SECONDS.toMillis(10))

        if(delete){
            println("Cleaner:Size of the queue :${deque.size}")
        }

    }
}


fun main(args: Array<String>) {
    val deque = LinkedBlockingDeque<Event>()
    val writeTask = WriteTask(deque)
    for(index in 0 until 3){
        val thread = Thread(writeTask)
        thread.start()
    }

    val cleanerTask = CleanerTask(deque)
    cleanerTask.start()

    val char = '国'
    val english = 'a'
    val any = Any()
    val obj = java.lang.Object()

    val utf = "a".toByteArray()
    val iso88591 = "a".toByteArray(Charsets.ISO_8859_1)
    println("char size:${ObjectSizeCalculator.getObjectSize(char)}")
    println("english size:${ObjectSizeCalculator.getObjectSize(english)}")
    println("any size:${ObjectSizeCalculator.getObjectSize(any)}")
    println("object size:${ObjectSizeCalculator.getObjectSize(obj)}")
    println("utf size:${ObjectSizeCalculator.getObjectSize(utf)}")
    println("iso88591 size:${ObjectSizeCalculator.getObjectSize(iso88591)}")
}
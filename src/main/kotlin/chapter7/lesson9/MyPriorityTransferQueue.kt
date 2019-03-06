package chapter7.lesson9

import extentions.notifyK
import extentions.waitK
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.TransferQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

private class MyPriorityTransferQueue<E> : PriorityBlockingQueue<E>(),TransferQueue<E>{

    private val counter = AtomicInteger()
    private val lock = ReentrantLock()
    private val transfered = LinkedBlockingQueue<E>()

    override fun getWaitingConsumerCount(): Int {
        return counter.get()
    }

    override fun tryTransfer(e: E): Boolean {
        lock.lock()
        var value = false
        if(counter.get() == 0){
            value = false
        } else {
            put(e)
            value = true
        }

        lock.unlock()
        return value
    }

    override fun tryTransfer(e: E, timeout: Long, unit: TimeUnit?): Boolean {
        lock.lock()
        if(counter.get() != 0){
            put(e)
            lock.unlock()
            return true
        }else{
            transfered.add(e)
            val newTimeout = TimeUnit.MILLISECONDS.convert(timeout,unit)
            lock.unlock()
            val any = e as Any
            any.waitK(newTimeout)

            lock.lock()

            if(transfered.contains(e)){
                transfered.remove(e)
                lock.unlock()
                return false
            }else{
                lock.unlock()
                return true
            }
        }
    }

    override fun transfer(e: E) {
        lock.lock()
        if(counter.get() != 0){
            put(e)
            lock.unlock()
        }else{
            transfered.put(e)
            lock.unlock()
            val any = e as Any
            synchronized(any){
                any.waitK()
            }
        }
    }

    override fun hasWaitingConsumer(): Boolean {
        return (counter.get() != 0)
    }

    override fun take(): E {
        lock.lock()
        counter.incrementAndGet()

        var value:E? = transfered.poll()
        if(value == null){
            lock.unlock()
            value = super.take()
            lock.lock()
        }else{
            var any = (value as Any)!!
            synchronized(any){
                any.notifyK()
            }
        }

        counter.decrementAndGet()
        lock.unlock()
        return value!!
    }
}

private class Event(val thread:String,
                    val priority:Int):Comparable<Event>{
    override fun compareTo(other: Event): Int {
        return when {
            this.priority > other.priority -> -1
            this.priority < other.priority -> 1
            else -> 0
        }
    }
}

private class Producer(private val buffer:MyPriorityTransferQueue<Event>) : Runnable{
    override fun run() {
        for (index in 0 until 100){
            val event = Event(Thread.currentThread().name,index)
            buffer.put(event)
        }
    }
}

private class Consumer(private val buffer:MyPriorityTransferQueue<Event>) : Runnable{
    override fun run() {
        for (index in 0 until 1002){
            val take = this.buffer.take()
            println("Consumer:${take.thread} : ${take.priority}")
        }
    }
}

fun main(args: Array<String>) {
    val buffer = MyPriorityTransferQueue<Event>()

    val producer = Producer(buffer)
    val producerThreads = arrayOfNulls<Thread>(10)
    for (index in 0 until producerThreads.size){
        producerThreads[index] = Thread(producer)
        producerThreads[index]?.start()
    }

    val consumer = Consumer(buffer)
    val consumerThread = Thread(consumer)
    consumerThread.start()

    // 写入消费者数量
    println("Main:Buffer:Consumer count:${buffer.waitingConsumerCount}")

    // 使用 transfer() 方法传输一个事件给消费者。
    val myEvent = Event("Core Event",0)
    buffer.transfer(myEvent)

    println("Main:MyEvent has been transfer.")

    producerThreads.forEach { it?.join() }

    TimeUnit.SECONDS.sleep(1)

    val myEvent2 = Event("Core Event 2",0)
    buffer.transfer(myEvent2)

    consumerThread.join()
    println("Main:End of the program.")



}
package chapter7.lesson8

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.AbstractQueuedSynchronizer
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
// Java并发API提供一个类，可以用来实现拥有锁和信号量特征的同步机制。
// 它就是AbstractQueuedSynchronizer，正如其名，它是一个抽象类。
// 它提供控制临界区的访问和管理正在阻塞等待访问临界区的线程队列的操作。
// 这些操作是基于以下两个抽象方法：

// 它就是AbstractQueuedLongSynchronizer类，它与AbstractQueuedSynchronizer一样，
// 除了使用一个long类型属性来存储线程的状态。

private class MyAbstractQueuedSynchronizer : AbstractQueuedSynchronizer(){

    private val state = AtomicInteger(0)

    override fun tryAcquire(arg: Int): Boolean {
        // 试图将变量state的值从0变成1。如果成功，它将返回true，否则，返回false。
        return state.compareAndSet(0,1)
    }

    override fun tryRelease(arg: Int): Boolean {
        // 试图将变量sate的值从1变成0.如果成功，它将返回true，否则，返回false。
        return state.compareAndSet(1,0)
    }
}

private class MyLock : Lock {

    private val sync = MyAbstractQueuedSynchronizer()

    override fun lock() {
        sync.acquire(1)
    }

    override fun tryLock(): Boolean {
        return sync.tryAcquireNanos(1,1000)
    }

    override fun tryLock(time: Long, unit: TimeUnit?): Boolean {
        return sync.tryAcquireNanos(1,TimeUnit.NANOSECONDS.convert(time,unit))
    }

    override fun unlock() {
        sync.release(1)
    }

    override fun lockInterruptibly() {
        sync.acquireInterruptibly(1)
    }

    override fun newCondition(): Condition {
        return sync.ConditionObject()
    }
}

private class Task(private val name:String,
                   private val lock:MyLock):Runnable{
    override fun run() {
        try {
            lock.lock()
            println("Task: $name: Take the lock.")
            TimeUnit.SECONDS.sleep(2)
            println("Task: $name: Free the lock.")
        }catch (e:InterruptedException){
            e.printStackTrace()
        }finally {
            lock.unlock()
        }
    }
}

fun main(args: Array<String>) {
    val lock = MyLock()

    for (index in 0 until 10){
        val task = Task("Task $index",lock)
        val thread = Thread(task)
        thread.start()
    }

    var value = false
    do{
        value = lock.tryLock()
        if(!value){
            println("Main: Trying to get the lock.")
        }
    }while (!value)

    println("Main: Got the lock")
    lock.unlock()

    println("Main:End of the program.")
}
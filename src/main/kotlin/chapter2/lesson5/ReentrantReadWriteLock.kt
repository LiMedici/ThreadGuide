package chapter2.lesson5

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.system.exitProcess

private class PricesInfo{
    private var price1:Double = 1.0
    private var price2:Double = 2.0

    private val lock = ReentrantReadWriteLock()

    fun getPrice1():Double{
        lock.readLock().lock()
        val value = price1
        lock.readLock().unlock()
        return value
    }

    fun getPrice2():Double{
        lock.readLock().lock()
        val value = price2
        lock.readLock().unlock()
        return value
    }

    fun setPrices(price1:Double,price2:Double){
        lock.writeLock().lock()
        this.price1 = price1
        this.price2 = price2
        lock.writeLock().unlock()
    }
}


private class Reader(private val pricesInfo: PricesInfo) : Runnable{
    override fun run() {
        for (index in 0 until 10){
            println("${Thread.currentThread().name} : Price1 : ${pricesInfo.getPrice1()}")
            println("${Thread.currentThread().name} : Price2 : ${pricesInfo.getPrice2()}")
        }
    }
}

private class Writer(private val pricesInfo: PricesInfo) : Runnable{
    override fun run() {
        for (index in 0..3){
            println("Writer: Attempt to modify the prices")
            pricesInfo.setPrices(Math.random()*10,Math.random()*8)
            println("Writer: Prices have been modified.")

            try{
                Thread.sleep(2)
            }catch (e:InterruptedException){
                e.printStackTrace()
            }
        }
    }
}

fun main(args: Array<String>) {
    val pricesInfo = PricesInfo()
    val threads = arrayOfNulls<Thread>(5)
    for (index in 0 until threads.size) {
        threads[index] = Thread(Reader(pricesInfo))
    }

    val writerThread = Thread(Writer(pricesInfo))

    threads.forEach { it?.start() }
    writerThread.start()
}
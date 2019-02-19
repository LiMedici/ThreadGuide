package chapter2.lesson7

import extentions.waitK
import java.util.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

private class FileMock constructor(size:Int,length:Int){
    private var index:Int = 0
    private val content:Array<String?> = arrayOfNulls(size)

    init {
        for (index in 0 until size){
            val buffer = StringBuilder(length)
            for (count in 0 until length){
                val indice = (Math.random()*65535).toInt()
                buffer.append(indice.toChar())
            }
            content[index] = buffer.toString()
        }
    }

    fun hasMoreLines():Boolean{
        return index < content.size
    }

    fun getLine():String?{
        if(hasMoreLines()){
            println("Mock: ${content.size - index}")
            return content[index++]
        }

        return null
    }

}

private class Buffer(maxSize:Int){
    // 一个类型为LinkedList<String>，名为buffer的属性，用来存储共享数据
    private val buffer = LinkedList<String>()
    // 一个类型为int，名为maxSize的属性，用来存储缓冲区的长度
    private val maxSize:Int = maxSize
    // 一个名为lock的ReentrantLock对象，用来控制修改缓冲区代码块的访问
    private val lock:ReentrantLock = ReentrantLock()
    // 两个名分别为lines和space，类型为Condition的属性
    private val lines:Condition
    private val space:Condition
    // 一个Boolean类型，名为pendingLines的属性，表明如果缓冲区中有行
    private var peedingLines = false

    init {
        lines = lock.newCondition()
        space = lock.newCondition()
        peedingLines = true
    }

    fun insert(line:String){
        lock.lock()

        try {
            while (buffer.size == maxSize) {
                space.await()
            }
            buffer.offer(line)
            println("${Thread.currentThread().name} : Inserted Line:$line")
            lines.signalAll()
        }catch (e:InterruptedException){
            e.printStackTrace()
        }finally {
            lock.unlock()
        }
    }

    fun get():String?{

        var line:String? = null
        lock.lock()
        try{

            while(buffer.size == 0 && hasPendingLines()){
                lines.await()
            }

            if(hasPendingLines()){
                line = buffer.poll()
                println("${Thread.currentThread().name} Line Readed : ${buffer.size}")
                space.signalAll()
            }
        }catch (e:InterruptedException){
            e.printStackTrace()
        }finally {
            lock.unlock()
        }

        return line
    }

    fun setPendingLines(pendingLines:Boolean){
        this.peedingLines = peedingLines
    }

    fun hasPendingLines():Boolean{
        return peedingLines || buffer.size > 0
    }
}

private class Producer(private val mock:FileMock,private val buffer:Buffer) : Runnable{
    override fun run() {
        buffer.setPendingLines(true)
        while(mock.hasMoreLines()){
            val line = mock.getLine()
            buffer.insert(line!!)
        }
        buffer.setPendingLines(false)
    }
}

private class Consumer(private val buffer:Buffer) : Runnable{
    override fun run() {
        while(buffer.hasPendingLines()){
            val line = buffer.get()
            processLine(line)
        }
    }

    fun processLine(line:String?){
        try {
            val random = Random()
            Thread.sleep(random.nextInt(100).toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}

fun main(args: Array<String>) {
    val mock = FileMock(100,10)
    // 创建一个Buffer对象
    val buffer = Buffer(20)

    val producer = Producer(mock,buffer)
    val threadProducer = Thread(producer,"Producer")

    val consumers = arrayOfNulls<Consumer>(3)
    val threadConsumers = arrayOfNulls<Thread>(3)

    for(index in 0 until 3){
        consumers[index] = Consumer(buffer)
        threadConsumers[index] = Thread(consumers[index],"Consumer $index")
    }

    threadProducer.start()
    threadConsumers.forEach { it?.start() }


}

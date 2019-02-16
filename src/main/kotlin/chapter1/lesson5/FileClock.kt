package chapter1.lesson5

import java.util.*
import java.util.concurrent.TimeUnit

class FileClock : Runnable{

    override fun run() {
        for (index in 0 until 10){
            println("${Date()}")

            // 睡眠1秒
            try{
                TimeUnit.SECONDS.sleep(1)
            }catch (e:InterruptedException){
                e.printStackTrace()
                println("The FileClock has been interrupted")
            }
        }
    }
}

fun main(args: Array<String>) {
    val thread = Thread(FileClock())
    thread.start()

    try {
        TimeUnit.SECONDS.sleep(5)
    } catch (e:InterruptedException) {
        e.printStackTrace()
    }
    // 中断线程
    thread.interrupt()

}
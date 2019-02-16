package chapter1.lesson6

import java.util.*
import java.util.concurrent.TimeUnit

class DataSourcesLoader : Runnable{
    override fun run() {
        println("Beginning data sources loading:${Date()}")
        try{
            TimeUnit.SECONDS.sleep(4)
        }catch (e:InterruptedException){
            e.printStackTrace()
        }

        println("Data sources loading has finished:${Date()}")
    }
}

class NetworkConnectionsLoader : Runnable{
    override fun run() {
        println("Network connect loading:${Date()}")
        try{
            TimeUnit.SECONDS.sleep(6)
        }catch (e:InterruptedException){
            e.printStackTrace()
        }

        println("Network connect loading has finished:${Date()}")
    }
}

fun main(args: Array<String>) {
    val thread1 = Thread(DataSourcesLoader())
    val thread2 = Thread(NetworkConnectionsLoader())

    thread1.start()
    thread2.start()

    try{
        thread1.join()
        thread2.join()
    }catch (e:InterruptedException){
        e.printStackTrace()
    }

    println("Main:Configuration has been loaded:${Date()}")
}
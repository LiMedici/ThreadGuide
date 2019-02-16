package chapter1.lesson2

import chapter1.lesson1.Calculator
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

fun main(args: Array<String>) {
    val threads = arrayOfNulls<Thread>(10)
    val status = arrayOfNulls<Thread.State>(10)
    val file = File(".\\data\\log.txt")

    if(!file.exists()){
        if(!file.parentFile.exists()) file.parentFile.mkdirs()
        file.createNewFile()
    }

    val fileWriter = FileWriter(file)
    val printWriter = PrintWriter(fileWriter)
    for (index in 0 until 10){
        threads[index] = Thread(Calculator(index))
        if(index % 2 == 0){
            threads[index]!!.priority = Thread.MAX_PRIORITY
        }else{
            threads[index]!!.priority = Thread.MIN_PRIORITY
        }

        threads[index]!!.name = "Thread $index"

        printWriter.println("Main : Status of Thread $index : ${threads[index]!!.state}")
        status[index] = threads[index]!!.state
    }

    for (index in 0 until 10) threads[index]!!.start()

    var finish = false
    while(!finish){
        for(index in 0 until 10){
            if(threads[index]!!.state != status[index]){
                // 更新Log文件State
                writeThreadInfo(printWriter,threads[index]!!,status[index]!!)
                status[index] = threads[index]!!.state
                threads[index]!!.interrupt()
            }
        }

        finish = true
        for(index in 0 until 10){
            finish = finish && (threads[index]!!.state == Thread.State.TERMINATED)
        }
    }

    printWriter.flush()
    printWriter.close()

}



fun writeThreadInfo(printWriter: PrintWriter,thread: Thread,state: Thread.State){
    printWriter.apply {
        println("Main : Id ${thread.id} , Name ${thread.name}")
        println("Main : Priority ${thread.priority}")
        println("Main : Old State $state")
        println("Main : New State ${thread.state}")
        println("Main : ======================================")
    }
}
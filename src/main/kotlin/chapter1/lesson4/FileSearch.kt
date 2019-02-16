package chapter1.lesson4

import java.io.File
import java.util.concurrent.TimeUnit

class FileSearch constructor(private val initPath:String,private val fileName:String) : Runnable{
    override fun run() {
        val file = File(initPath)
        if(file.isDirectory){
            try {
                directoryProcess(file)
            } catch (e:InterruptedException) {
                // 程序受到中断
                println("${Thread.currentThread().name}: The search has been interrupted")
            }
        }
    }

    @Throws(InterruptedException::class)
    private fun directoryProcess(file: File) {
        val list = file.listFiles()
        if (list != null) {
            for (i in list.indices) {
                if (list[i].isDirectory) {
                    directoryProcess(list[i])
                } else {
                    fileProcess(list[i])
                }
            }
        }

        if (Thread.interrupted()) {
            throw InterruptedException()
        }
    }

    @Throws(InterruptedException::class)
    private fun fileProcess(file: File) {
        if (file.name == fileName) {
            System.out.printf("%s : %s\n", Thread.currentThread().name, file.absolutePath)
        }

        if (Thread.interrupted()) {
            throw InterruptedException()
        }
    }
}

fun main(args: Array<String>) {
    val fileSearch = FileSearch("E:\\", "BooleanExt.kt")
    val thread = Thread(fileSearch)
    thread.start()

    try {
        TimeUnit.SECONDS.sleep(3)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }


    thread.interrupt()
}
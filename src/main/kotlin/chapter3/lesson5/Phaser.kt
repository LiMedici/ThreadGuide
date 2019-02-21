package chapter3.lesson5

import java.io.File
import java.util.*
import java.util.concurrent.Phaser
import java.util.concurrent.TimeUnit

private class FileSearch(
    private val initPath: String,
    private val end: String,
    private val phaser: Phaser
) : Runnable {

    private val results = ArrayList<String>()

    override fun run() {
        phaser.arriveAndAwaitAdvance()

        println("${Thread.currentThread().name} : Starting.")

        val file = File(initPath)
        if (file.isDirectory) directoryProcess(file)

        if (!checkResults()) return

        filterResults()

        if (!checkResults()) return

        showInfo()

        phaser.arriveAndDeregister()

        println("${Thread.currentThread().name} : Work completed.")
    }

    private fun directoryProcess(file: File) {
        val listFile = file.listFiles()
        listFile?.forEach {
            if (it.isDirectory) {
                directoryProcess(it)
            } else {
                fileProcess(it)
            }
        }
    }


    private fun fileProcess(file: File) {
        if (file.name.endsWith(end)) {
            results.add(file.absolutePath)
        }
    }

    private fun filterResults() {
        val actualDate = Date().time

        results.map {
            File(it)
        }.filter {
            actualDate - it.lastModified() < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
        }.map {
            it.absolutePath
        }
    }

    private fun checkResults(): Boolean {
        return if (results.isEmpty()) {
            println("${Thread.currentThread().name} : Phase ${phaser.phase} : 0 results.")
            println("${Thread.currentThread().name} : Phase ${phaser.phase} : End")
            phaser.arriveAndDeregister()
            false
        } else {
            println("${Thread.currentThread().name} : Phase ${phaser.phase} : ${results.size} results.")
            phaser.arriveAndAwaitAdvance()
            true
        }
    }

    private fun showInfo() {
        results.map {
            File(it)
        }.forEach { println("${Thread.currentThread().name} : ${it.absolutePath}") }
    }
}

fun main(args: Array<String>) {
    val phaser = Phaser(3)

    val system = FileSearch("C:\\Windows", "log", phaser)
    val apps = FileSearch("C:\\Program Files", "log", phaser)
    val intel = FileSearch("C:\\Intel", "log", phaser)

    val systemThread = Thread(system, "System")
    systemThread.start()
    val appsThread = Thread(apps, "apps")
    appsThread.start()
    val intelThread = Thread(intel, "intel")
    intelThread.start()

    systemThread.join()
    appsThread.join()
    intelThread.join()

    println("Terminated : ${phaser.isTerminated}")

}
package chapter5.lesson3

import java.io.File
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.concurrent.TimeUnit

private class FolderProcessor(private val path:String,
                              private val extension:String) : RecursiveTask<List<String>>(){

    override fun compute(): List<String> {
        val list = ArrayList<String>()

        val tasks = ArrayList<FolderProcessor>()

        val root = File(path)
        val content = root.listFiles()
        content?.forEach {
            if(it.isDirectory) {
                val task = FolderProcessor(it.absolutePath, extension)
                task.fork()
                tasks.add(task)
            }else{
                if(checkFile(it.name)){
                    list.add(it.absolutePath)
                }
            }
        }

        if(tasks.size > 50){
            println("${root.absolutePath}: ${tasks.size} tasks ran.")
        }

        addResultFromTasks(list,tasks)

        return list
    }

    private fun checkFile(path:String):Boolean{
        return path.endsWith(extension)
    }

    private fun addResultFromTasks(list:MutableList<String>,tasks:List<FolderProcessor>){
        tasks.forEach {
            list.addAll(it.join())
        }
    }
}

fun main(args: Array<String>) {
    val pool = ForkJoinPool()
    val system = FolderProcessor("C:\\Windows","log")
    val apps = FolderProcessor("C:\\Program Files","log")
    val documents = FolderProcessor("C:\\Documents And Settings","log")

    pool.execute(system)
    pool.execute(apps)
    pool.execute(documents)

    do {
        println("******************************************")
        println("Main: Parallelism: ${pool.parallelism}")
        println("Main: Active Threads: ${pool.activeThreadCount}")
        println("Main: Task Count: ${pool.queuedTaskCount}")
        println("Main: Steal Count: ${pool.stealCount}")
        println("******************************************")
            try {
            TimeUnit.SECONDS.sleep(1)
        } catch (e:InterruptedException) {
            e.printStackTrace()
        }
    } while((!system.isDone)||(!apps.isDone)||(!documents.isDone))

    var results = system.join()
    println("System: ${results.size} files found.")
    results = apps.join()
    println("Apps: ${results.size} files found.")
    results = documents.join()
    println("Documents: ${results.size} files found.")

}
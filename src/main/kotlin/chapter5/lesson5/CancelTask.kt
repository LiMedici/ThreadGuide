package chapter5.lesson5

import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.concurrent.TimeUnit

// ForkJoinPool类并没有提供任何方法来取消正在池中运行或等待的所有任务。
// 当你取消一个任务时，你不能取消一个已经执行的任务。
private class ArrayGenerator{
    fun generatorArray(size:Int):IntArray{
        val array = IntArray(size)
        val random = Random()
        for(index in 0 until array.size){
            array[index] = random.nextInt(10)
        }

        return array
    }
}

private class TaskManager{
    private val tasks = ArrayList<RecursiveTask<Int>>()

    fun addTask(task:RecursiveTask<Int>){
        tasks.add(task)
    }

    fun cancelTasks(vararg tasks:RecursiveTask<Int>){
        this.tasks.filter { !tasks.contains(it) }.forEach {
            it.cancel(true)
            (it as SearchNumberTask).writeCancelMessage()
        }
    }
}

private class SearchNumberTask(private val manager: TaskManager,
                               private val numbers:IntArray,
                               private val start:Int,
                               private val end:Int,
                               private val number:Int) : RecursiveTask<Int>(){
    override fun compute(): Int {
        return if(end - start > 10){
            launchTasks()
        }else{
            lookForNumber()
        }
    }

    private fun lookForNumber():Int{
        for(index in start until end){
            if(numbers[index] == number) {
                println("Task: Number $number found in position $index")
                manager.cancelTasks(this)
                return index
            }

            try{
                TimeUnit.SECONDS.sleep(1)
            }catch (e:InterruptedException){
                e.printStackTrace()
            }
        }

        return -1
    }

    private fun launchTasks():Int{
        val middle = (start + end) / 2
        val task1 = SearchNumberTask(manager,numbers,start,middle,number)
        val task2 = SearchNumberTask(manager,numbers,middle,end,number)
        manager.addTask(task1)
        manager.addTask(task2)
        task1.fork()
        task2.fork()

        var returnValue:Int = task1.join()
        if(returnValue != -1) return returnValue
        return task2.join()

    }

    fun writeCancelMessage(){
        println("Task: Canceled task from $start to $end")
    }
}

fun main(args: Array<String>) {
    val generator = ArrayGenerator()
    val array = generator.generatorArray(1000)
    val manager = TaskManager()
    val task = SearchNumberTask(manager,array,0,array.size,5)

    val pool = ForkJoinPool()
    pool.execute(task)

    pool.shutdown()

    pool.awaitTermination(1,TimeUnit.DAYS)
    println("Main:The program has finished.")
}
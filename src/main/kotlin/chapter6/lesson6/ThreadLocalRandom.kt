package chapter6.lesson6

import java.util.concurrent.ThreadLocalRandom

private class TaskLocalRandom : Runnable{

    init {
        ThreadLocalRandom.current()
    }

    override fun run() {
        val name = Thread.currentThread().name
        for(index in 0 until 10){
            println("$name: ${ThreadLocalRandom.current().nextInt(10)}")
        }
    }


}


fun main(args: Array<String>) {
    val threads = arrayOfNulls<Thread>(3)
    for (index in 0 until threads.size){
        threads[index] = Thread(TaskLocalRandom())
        threads[index]?.start()
    }
}
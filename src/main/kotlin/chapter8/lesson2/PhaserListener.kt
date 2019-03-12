package chapter8.lesson2

import java.util.concurrent.Phaser
import java.util.concurrent.TimeUnit

private class Task(private val time:Int,
                   private val phaser: Phaser) : Runnable{
    override fun run() {
        phaser.arrive()
        println("${Thread.currentThread().name}:Entering phase 1.")
        TimeUnit.SECONDS.sleep(time.toLong())
        println("${Thread.currentThread().name}:Finishing phase 1.")
        phaser.arriveAndAwaitAdvance()

        println("${Thread.currentThread().name}:Entering phase 2.")
        TimeUnit.SECONDS.sleep(time.toLong())
        println("${Thread.currentThread().name}:Finishing phase 2.")
        phaser.arriveAndAwaitAdvance()

        println("${Thread.currentThread().name}:Entering phase 3.")
        TimeUnit.SECONDS.sleep(time.toLong())
        println("${Thread.currentThread().name}:Finishing phase 3.")
        phaser.arriveAndDeregister()
    }
}

fun main(args: Array<String>) {
    val phaser = Phaser(3)
    for (index in 0 until 3){
        val task = Task(index + 1,phaser)
        val thread = Thread(task)
        thread.start()
    }

    for (index in 0 until 10){
        println("***********************")
        println("Main:Phaser Log")
        println("Main:Phaser:Phase:${phaser.phase}")
        println("Main:Phaser:Registered Parties:${phaser.registeredParties}")
        println("Main:Phaser:Arrived Parties:${phaser.arrivedParties}")
        println("Main:Phaser:UnArrived Parties:${phaser.unarrivedParties}")
        println("***********************")

        TimeUnit.SECONDS.sleep(1)
    }
}
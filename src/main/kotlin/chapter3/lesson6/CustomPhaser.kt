package chapter3.lesson6

import java.util.*
import java.util.concurrent.Phaser
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

private class CustomPhaser : Phaser(){

    override fun onAdvance(phase: Int, registeredParties: Int): Boolean {
        return when(phase){
            0 -> studentArrived()
            1 -> finishFirstExercise()
            2 -> finishSecondExercise()
            3 -> finishExam()
            else -> true
        }
    }

    private fun studentArrived():Boolean{
        println("Phaser : The exam are going to start. The students are ready.")
        println("Phaser : We have $registeredParties students.")
        return false
    }

    private fun finishFirstExercise():Boolean{
        println("Phaser : All the students have finished the first exercise.")
        println("Phaser : It's time for the second one.")
        return false
    }


    private fun finishSecondExercise():Boolean{
        println("Phaser : All the students have finished the second exercise.")
        println("Phaser : It's time for the third one.")
        return false
    }

    private fun finishExam():Boolean{
        println("Phaser : All the students have finished the exam.")
        println("Phaser : Thank you for your time.")
        return true
    }

}


private class Student(private val phaser:Phaser) : Runnable{
    override fun run() {
        println("${Thread.currentThread().name} : Has arrived to do the exam. ${Date()}")

        phaser.arriveAndAwaitAdvance()

        println("${Thread.currentThread().name} : Is going to do the first exercise. ${Date()}")
        doExercise1()
        println("${Thread.currentThread().name} : Has done the first exercise. ${Date()}")
        phaser.arriveAndAwaitAdvance()

        println("${Thread.currentThread().name} : Is going to do the second exercise. ${Date()}")
        doExercise2()
        println("${Thread.currentThread().name} : Has done the second exercise. ${Date()}")
        phaser.arriveAndAwaitAdvance()

        println("${Thread.currentThread().name} : Is going to do the third exercise. ${Date()}")
        doExercise3()
        println("${Thread.currentThread().name} : Has finished the exam. ${Date()}")
        phaser.arriveAndAwaitAdvance()

    }

    private fun doExercise1(){
        val duration = (Math.random() * 10).toLong()
        TimeUnit.SECONDS.sleep(duration)
    }

    private fun doExercise2(){
        val duration = (Math.random() * 10).toLong()
        TimeUnit.SECONDS.sleep(duration)
    }

    private fun doExercise3(){
        val duration = (Math.random() * 10).toLong()
        TimeUnit.SECONDS.sleep(duration)
    }

}

fun main(args: Array<String>) {
    val phaser = Phaser()

    val students = arrayOfNulls<Student>(5)
    for(index in 0 until students.size){
        students[index] = Student(phaser)
        phaser.register()
    }

    val threads = arrayOfNulls<Thread>(students.size)
    for(index in 0 until threads.size){
        threads[index] = Thread(students[index])
        threads[index]!!.start()
    }

    threads.forEach { it!!.join() }

    println("Main : The phaser has finished: ${phaser.isTerminated}")
}
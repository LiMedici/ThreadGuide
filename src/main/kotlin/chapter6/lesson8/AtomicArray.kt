package chapter6.lesson8

import java.util.concurrent.atomic.AtomicIntegerArray


// get(int i): 返回array中第i个位置上的值
// set(int I, int newValue): 设置array中第i个位置上的值为newValue
private class Incrementer(private val vector:AtomicIntegerArray) : Runnable{
    override fun run() {
        for (index in 0 until vector.length()){
            vector.getAndIncrement(index)
        }
    }
}

private class Decrementer(private val vector:AtomicIntegerArray) : Runnable{
    override fun run() {
        for (index in 0 until vector.length()){
            vector.getAndDecrement(index)
        }
    }
}

fun main(args: Array<String>) {
    val THERADS = 100
    val vector = AtomicIntegerArray(1000)
    val incrementer = Incrementer(vector)
    val decrementer = Decrementer(vector)

    val threadIncrementer = arrayOfNulls<Thread>(THERADS)
    val threadDecrementer = arrayOfNulls<Thread>(THERADS)

    for (index in 0 until THERADS){
        threadIncrementer[index] = Thread(incrementer)
        threadDecrementer[index] = Thread(decrementer)

        threadIncrementer[index]?.start()
        threadDecrementer[index]?.start()
    }

    threadIncrementer.forEach { it?.join() }
    threadDecrementer.forEach { it?.join() }

    for (index in 0 until vector.length()){
        if(vector.get(index) != 0){
            println("Vector[$index]: ${vector.get(index)}")
        }
    }

    println("Main:End of the example")
}
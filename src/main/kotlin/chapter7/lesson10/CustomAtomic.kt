package chapter7.lesson10

import java.util.concurrent.atomic.AtomicInteger

private class ParkingCounter constructor(private val maxNumber:Int) : AtomicInteger(){

    override fun toByte(): Byte {
        return 0
    }

    override fun toChar(): Char {
        return 'A'
    }

    override fun toShort(): Short {
        return 5
    }

    fun carIn():Boolean{
        while (true){
            var value = get()
            if(value == maxNumber){
                println("ParkingCounter: The parking lot is full.")
                return false
            }else{
                val newValue = value + 1
                val changed = compareAndSet(value,newValue)
                if(changed){
                    println("ParkingCounter: A car has entered.")
                    return true
                }
            }
        }
    }

    fun carOut():Boolean{
        while (true){
            val value = get()
            if(value == 0){
                println("ParkingCounter:The parking lot is empty.")
                return false
            }else{
                val newValue = value - 1
                val changed = compareAndSet(value,newValue)
                if(changed){
                    println("ParkingCounter : A car has gone out.")
                    return true
                }
            }
        }
    }
}

private class Sensor1(private val counter:ParkingCounter) : Runnable{

    override fun run() {
        counter.carIn()
        counter.carIn()
        counter.carIn()
        counter.carIn()
        counter.carOut()
        counter.carOut()
        counter.carOut()
        counter.carIn()
        counter.carIn()
        counter.carIn()
    }
}

private class Sensor2(private val counter:ParkingCounter) : Runnable{
    override fun run() {
        counter.carIn()
        counter.carOut()
        counter.carOut()
        counter.carIn()
        counter.carIn()
        counter.carIn()
        counter.carIn()
        counter.carIn()
        counter.carIn()
    }
}

fun main(args: Array<String>) {
    val counter = ParkingCounter(5)
    val sensor1 = Sensor1(counter)
    val sensor2 = Sensor2(counter)
    val thread1 = Thread(sensor1)
    val thread2 = Thread(sensor2)
    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()

    println("Main:Number of cars:${counter.get()}")
    println("Main:End of the program.")
}
package manager.lesson1

class Calculator constructor(private val number:Int):Runnable{

    @Volatile private var flag = false

    override fun run() {
        for (index in 1 .. 10){
            if(!Thread.currentThread().isInterrupted){
                println("${Thread.currentThread().name}:$number * $index = ${number * index}")
            }
        }
    }
}

fun main(args: Array<String>) {
    for (index in 1..10){
        val calculator = Calculator(index)
        val thread = Thread(calculator)
        thread.start()
    }
}
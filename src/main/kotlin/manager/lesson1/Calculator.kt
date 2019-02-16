package manager.lesson1

class Calculator constructor(val number:Int):Runnable{

    override fun run() {
        for (index in 1 .. 10){
            println("${Thread.currentThread().name}:$number * $index = ${number * index}")
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
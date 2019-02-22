package chapter3.lesson7

import java.util.concurrent.Exchanger

private class Producer(private var buffer:MutableList<String>,
                       private val exchanger:Exchanger<MutableList<String>>) : Runnable{

    override fun run() {
        var cycle = 1
        for(i in 0 until 10){
            println("Producer : Cycle $cycle")
            for(j in 0 until 10){
                val message = "Event ${i * 10 + j}"
                println("Producer: $message")
                buffer.add(message)
            }

            try {
                buffer = exchanger.exchange(buffer)
            } catch (e : InterruptedException) {
                e.printStackTrace()
            }
            println("Producer: ${buffer.size}")
            cycle ++
        }
    }

}

private class Consumer(private var buffer:MutableList<String>,
                       private val exchanger:Exchanger<MutableList<String>>) : Runnable{
    override fun run() {
        var cycle = 1
        for(i in 0 until 10){
            println("Consumer : Cycle $cycle")
            try {
                buffer = exchanger.exchange(buffer)
            } catch (e:InterruptedException) {
                e.printStackTrace()
            }

            for (j in 0 until 10){
                val message = buffer[0]
                println("Consumer: $message")
                buffer.removeAt(0)
            }

            cycle++
        }
    }
}

fun main(args: Array<String>) {
    val buffer1 = ArrayList<String>()
    val buffer2 = ArrayList<String>()

    val exchanger = Exchanger<MutableList<String>>()

    val producer = Producer(buffer1,exchanger)
    val consumer = Consumer(buffer2,exchanger)

    val threadProducer = Thread(producer)
    val threadConsumer = Thread(consumer)

    threadProducer.start()
    threadConsumer.start()
}
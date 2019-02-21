package chapter3.lesson3

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private class VideoConference(number: Int) : Runnable{

    private val countDownLatch = CountDownLatch(number)

    fun arrive(name:String){
        println("$name has arrived.")
        countDownLatch.countDown()
        println("VideoConference: Waiting for ${countDownLatch.count} participants")
    }

    override fun run() {
        println("VideoConference: Initialization: ${countDownLatch.count} participants")

        try{
            countDownLatch.await()
            println("VideoConference: All the participants have come")
            println("VideoConference: Let's start...")

        }catch (e:InterruptedException){
            e.printStackTrace()
        }
    }
}

private class Participant(private val conference: VideoConference,private val name:String) : Runnable{
    override fun run() {
        val duration = (Math.random() * 10).toLong()
        TimeUnit.SECONDS.sleep(duration)
        conference.arrive(name)
    }
}


fun main(args: Array<String>) {
    val conference = VideoConference(10)

    val thread = Thread(conference)
    thread.start()

    for (index in 0 until 10){
        val participant = Participant(conference,"Participant:$index")
        val thread = Thread(participant)
        thread.start()
    }
}
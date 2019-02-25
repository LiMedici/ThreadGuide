package chapter4.lesson10

import java.util.concurrent.*

class ReportGenerator(private val sender:String,
                      private val title:String) : Callable<String>{
    override fun call(): String {
        val duration = (Math.random() * 10).toLong()
        println("${sender}_$title: ReportGenerator: Generating a report during $duration seconds")
        TimeUnit.SECONDS.sleep(duration)
        val ret = "$sender: $title"
        return ret
    }
}

class ReportRequest(private val name:String,
                    private val service:CompletionService<String>) : Runnable{
    override fun run() {
        val generator = ReportGenerator(name,"Report")
        service.submit(generator)
    }
}

class ReportProcessor(private val service: CompletionService<String>) : Runnable{

    @Volatile
    private var end = false

    override fun run() {
        while (!end){
            try{
                val result = service.poll(20,TimeUnit.SECONDS)
                if(result != null){
                    val report = result.get()
                    println("ReportReceiver : Report Received:$report")
                }
            }catch (e:InterruptedException){
                e.printStackTrace()
            }catch (e:ExecutionException){
                e.printStackTrace()
            }

            println("ReportSender:End")
        }
    }

    fun setEnd(end:Boolean){
        this.end = end
    }
}

fun main(args: Array<String>) {
    val executor = Executors.newCachedThreadPool() as ThreadPoolExecutor
    val service = ExecutorCompletionService<String>(executor)
    val faceRequest = ReportRequest("Face",service)
    val onlineRequest = ReportRequest("Online",service)
    val faceThread = Thread(faceRequest)
    val onlineThread = Thread(onlineRequest)

    val processor = ReportProcessor(service)
    val processorThread = Thread(processor)
    faceThread.start()
    onlineThread.start()
    processorThread.start()

    println("Main:Waiting for the report generators.")
    faceThread.join()
    onlineThread.join()

    println("Main:Shutting down the executor.")
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.DAYS)

    processor.setEnd(true)
    println("Main: Ends")


}
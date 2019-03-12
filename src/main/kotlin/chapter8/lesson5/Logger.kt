package chapter8.lesson5

import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.*
import java.util.logging.Formatter

private class MyFormatter : Formatter(){
    override fun format(record: LogRecord?): String {
        val sb = StringBuilder()
        sb.append("[${record?.level}] - ")
        sb.append("${Date(record!!.millis)} : ")
        sb.append("${record?.sourceClassName}.${record?.sourceMethodName} : ")
        sb.append("${record?.message}")
        return sb.toString()
    }
}

private class MyLogger{
    companion object {
        private var handler:Handler? = null

        fun getLogger(name:String):Logger{
            val logger = Logger.getLogger(name)
            logger.level = Level.ALL

            if(handler == null){
                handler = FileHandler("recipe8.log")
                val formatter = MyFormatter()
                handler?.formatter = formatter
            }

            if(logger.handlers.isEmpty()){
               logger.addHandler(handler)
            }

            return logger
        }
    }
}


private class Task:Runnable{
    override fun run() {
        val logger = MyLogger.getLogger(this.javaClass.name)

        logger.entering(Thread.currentThread().name,"run()")

        TimeUnit.SECONDS.sleep(2)

        logger.exiting(Thread.currentThread().name,"run()",Thread.currentThread())
    }
}


fun main(args: Array<String>) {
    val logger = MyLogger.getLogger("Core")
    logger.entering("Core","Main()",args)

    val threads = arrayOfNulls<Thread>(5)
    for (index in 0 until threads.size){
        logger.log(Level.INFO,"Launching thread:$index")
        val task = Task()
        threads[index] = Thread(task)
        logger.log(Level.INFO,"Thread created: ${threads[index]!!.name}")
        threads[index]!!.start()
    }

    logger.log(Level.INFO,"The Threads created. Waiting for its finalization")

    threads.forEach {
        it?.join()
        logger.log(Level.INFO,"Thread has finished its execution",it!!)
    }

    logger.exiting("Core","Main()")
}
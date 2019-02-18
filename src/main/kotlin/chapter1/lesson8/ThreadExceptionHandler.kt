package chapter1.lesson8

private class Task : Runnable{
    override fun run() {
        val number = "TTT".toInt()
    }
}

private class ExceptionHandler : Thread.UncaughtExceptionHandler{
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        println("An exception ha been captured")
        println("Thread:${t?.id}")
        println("Exception:${e?.javaClass?.name}:${e?.message}")
        println("Stack Trace:")
        e?.printStackTrace(System.out)
        println("Thread Status:${t?.state}")
    }
}

fun main(args: Array<String>) {
    val task = Task()
    val thread = Thread(task)
    thread.uncaughtExceptionHandler = ExceptionHandler()
    thread.start()

}
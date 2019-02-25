package chapter5.lesson2

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.concurrent.TimeUnit

private class DocumentMock {
    var mCounter:Int = 0

    private val words =
        arrayOf("the", "hello", "goodbye", "packet", "java", "thread", "pool", "random", "class", "main")

    fun generateDocument(numLines: Int, numWords: Int, word: String): Array<Array<String>> {
        var counter = 0
        val document = Array(numLines) { Array(numWords) { "" } }
        val random = java.util.Random()
        for (i in 0 until numLines) {
            for (j in 0 until numWords) {
                val index = random.nextInt(words.size)
                document[i][j] = words[index]
                if (document[i][j] == word) counter++
            }
        }

        println("DocumentMock: The word appears $counter times in the document")
        this.mCounter = counter
        return document
    }
}

private class LineTask(
    private val line: Array<String>,
    private val start: Int,
    private val end: Int,
    private val word: String
) : RecursiveTask<Int>() {
    override fun compute(): Int {
        return if (end - start < 100) {
            count(line, start, end, word)
        } else {
            val middle = (start + end) / 2
            val task1 = LineTask(line, start, middle, word)
            val task2 = LineTask(line, middle, end, word)
            invokeAll(task1, task2)

            groupResults(task1.get(), task2.get())
        }

    }

    private fun count(line: Array<String>,
                      start: Int,
                      end: Int,
                      word: String):Int {
        var counter = 0
        for(index in start until end){
            if(line[index] == word){
                counter++
            }
        }

        TimeUnit.MILLISECONDS.sleep(10)

        return counter
    }


    private fun groupResults(number1: Int, number2: Int): Int {
        return number1 + number2
    }
}


private class DocumentTask(
    private val document: Array<Array<String>>,
    private val start: Int,
    private val end: Int,
    private val word: String
) : RecursiveTask<Int>() {
    override fun compute(): Int {

        return if (end - start < 10) {
            processLines(document, start, end, word)
        } else {
            val middle = (start + end) / 2
            val task1 = DocumentTask(document, start, middle, word)
            val task2 = DocumentTask(document, middle, end, word)
            invokeAll(task1, task2)

            groupResults(task1.get(), task2.get())
        }
    }

    private fun processLines(
        document: Array<Array<String>>,
        start: Int,
        end: Int,
        word: String
    ): Int {
        val tasks = ArrayList<LineTask>()
        for (index in start until end) {
            val task = LineTask(document[index], 0, document[index].size, word)
            tasks.add(task)
        }

        invokeAll(tasks)

        var result = 0
        tasks.forEach { result += it.get() }
        return result
    }

    private fun groupResults(number1: Int, number2: Int): Int {
        return number1 + number2
    }
}

fun main(args: Array<String>) {
    val mock = DocumentMock()
    val document = mock.generateDocument(100,1000,"the")

    val task = DocumentTask(document,0,100,"the")

    val pool = ForkJoinPool()
    pool.execute(task)

    do{

        println("Main: Parallelism: ${pool.parallelism}")
        println("Main: Thread Count: ${pool.activeThreadCount}")
        println("Main: Thread Task Count: ${pool.queuedTaskCount}")
        println("Main: Thread Steal: ${pool.stealCount}")
        TimeUnit.SECONDS.sleep(1)
    }while (!task.isDone)

    pool.shutdown()
    println("Main:The Word appears ${task.get()} in the document")
    println("Main:The Word appears ${mock.mCounter} in the document")
}
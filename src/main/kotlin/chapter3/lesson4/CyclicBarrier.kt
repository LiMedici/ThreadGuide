package chapter3.lesson4

import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CyclicBarrier



private class  MatrixMock(size:Int,length:Int,number:Int){
    private val data:Array<Array<Int>> = Array(size) { Array(length) { 0 } }


    init{
        var counter = 0
        val random = java.util.Random()

        for(i in 0 until size){
            for(j in 0 until length){
                data[i][j] = random.nextInt(10)
                if(data[i][j] == number){
                    counter++
                }
            }
        }

        println("Mock: There are $counter of $number in generated data.")
    }

    fun getRow(row:Int):Array<Int>?{
        if(row > 0 && row < data.size){
            return data[row]
        }

        return null
    }
}

private class Results(size:Int){
    private val data = Array(size){ 0 }

    fun setData(position:Int,value:Int) = {data[position] = value}

    fun getData():Array<Int> = data
}


private class Searcher(private val firstRow:Int,
                       private val lastRow:Int,
                       private val mock:MatrixMock,
                       private val results:Results,
                       private val number:Int,
                       private val barrier:CyclicBarrier) : Runnable{
    override fun run() {
        println("${Thread.currentThread().name} : Processing lines form $firstRow to $lastRow .")

        for (index in firstRow until lastRow){
            val row = mock.getRow(index)
            var counter = 0
            row?.forEach {
                if(it == number)
                    counter++
            }

            results.setData(index,counter)
        }

        println("${Thread.currentThread().name} : Lines Processed.")

        try {
            barrier.await()
        }catch (e:InterruptedException){
            e.printStackTrace()
        }catch (e:BrokenBarrierException){
            e.printStackTrace()
        }
    }
}

private class Grouper(private val results:Results) : Runnable{

    override fun run() {
        var finalResult = 0
        println("Grouper : Processing results")

        val data = results.getData()
        data.forEach { finalResult += it }

        println("Grouper : Total result : $finalResult")
    }
}


fun main(args: Array<String>) {
    val ROWS = 10000
    val NUMBERS = 1000
    val SEARCH = 5
    val PARTICIPANTS = 5
    val LINES_PARTICIPANT = 2000


    // Create a MatrixMock 对象,名为mock,它将有10000行,每行1000个元素。
    // 现在，你要查找的数字是5。
    val mock = MatrixMock(ROWS, NUMBERS, SEARCH)

    val results = Results(ROWS)

    val grouper = Grouper(results)

    val cyclicBarrier = CyclicBarrier(PARTICIPANTS,grouper)

    // 创建5个 Searcher 对象，5个执行他们的线程，并开始这5个线程。
    val searchers = arrayOfNulls<Searcher>(PARTICIPANTS)
    for(index in 0 until PARTICIPANTS){
        searchers[index] = Searcher(
            index * LINES_PARTICIPANT,
            index * LINES_PARTICIPANT + LINES_PARTICIPANT,
            mock,results,5,cyclicBarrier)
        val thread = Thread(searchers[index])
        thread.start()
    }

    println("Main: The main thread has finished.")


}
package chapter5.lesson1

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveAction
import java.util.concurrent.TimeUnit

private data class Product(var name:String,
                           var price:Double)

private class ProductListGenerator{
    fun generate(size:Int):List<Product>{
        val productList = ArrayList<Product>()
        for(index in 0 until size){
            val product = Product("Product $index",10.0)
            productList.add(product)
        }
        return productList
    }
}

private class Task(private val products:List<Product>,
                   private val first:Int,
                   private val last:Int,
                   private val increment:Double) : RecursiveAction(){
    private val serialVersionUID = 1L

    override fun compute() {
        if(last - first < 10){
            updatePrices()
        }else{
            val middle = (last + first) / 2
            println("Task: Pending tasks: ${ForkJoinTask.getQueuedTaskCount()}")
            val task1 = Task(products,first,middle + 1,increment)
            val task2 = Task(products,middle + 1,last,increment)
            invokeAll(task1,task2)
        }
    }

    fun updatePrices(){
        for(index in first until last) {
            val product = products[index]
            product.price = product.price * (1 + increment)
        }
    }
}

fun main(args: Array<String>) {
    val generator = ProductListGenerator()
    val products = generator.generate(10000)

    val task = Task(products,0,products.size,0.2)
    val pool = ForkJoinPool()
    pool.execute(task)

    do {
        println("Main: Thread Count: ${pool.activeThreadCount}")
        println("Main: Thread Steal: ${pool.stealCount}")
        println("Main: Parallelism: ${pool.parallelism}")

        try {
            TimeUnit.MILLISECONDS.sleep(5)
        } catch (e:InterruptedException ) {
            e.printStackTrace()
        }
    } while (!task.isDone)

    pool.shutdown()

    if (task.isCompletedNormally){
        println("Main: The process has completed normally.")
    }

    products.filter {
        it -> it.price != 12.0
    }.forEach {
        it -> println("Product ${it.name} : ${it.price}")
    }

    println("Main: End of the program.")

}
package manager.lessson3

class PrimeGenerator : Thread(){
    override fun run() {
        var number = 1L
        while (true){
            if(isPrime(number)){
                println("Number $number is Prime")
            }

            if(isInterrupted){
                // 发生中断操作
                println("The Prime Generator has been Interrupt")
                return
            }

            number++
        }
    }

    private fun isPrime(number:Long):Boolean{
        if(number < 2){
            return true
        }

        for (index in 2 until number){
            if((number % index) == 0L) return false
        }

        return true
    }
}

fun main(args: Array<String>) {
    val task = PrimeGenerator()
    task.start()

    Thread.sleep(5000)
    task.interrupt()
}
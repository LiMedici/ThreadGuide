package chapter2.lesson1

import java.util.concurrent.TimeUnit

private class Account(var balance:Double){

    @Synchronized
    fun addAmount(amount:Double){
        var balance = this.balance
        try {
            TimeUnit.MILLISECONDS.sleep(10)
        }catch (e:InterruptedException){
            e.printStackTrace()
        }

        balance += amount
        this.balance = balance
    }

    @Synchronized
    fun subtractAmount(amount:Double){
        var balance = this.balance
        try {
            TimeUnit.MILLISECONDS.sleep(10)
        }catch (e:InterruptedException){
            e.printStackTrace()
        }

        balance -= amount
        this.balance = balance
    }
}

private class Bank(private val account:Account) : Runnable{
    override fun run() {
        for(index in 0 until 100){
            account.subtractAmount(1000.toDouble())
        }
    }
}

private class Company(private val account:Account) : Runnable{
    override fun run() {
        for(index in 0 until 100){
            account.addAmount(1000.toDouble())
        }
    }
}

fun main(args: Array<String>) {
    val account = Account(1000.toDouble())
    val company = Company(account)
    val companyThread = Thread(company)
    val bank = Bank(account)
    val bankThread = Thread(bank)

    println("Account: Initial Balance:${account.balance}")

    companyThread.start()
    bankThread.start()

    companyThread.join()
    bankThread.join()

    println("Account : Final Balance:${account.balance}")

}





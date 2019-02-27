package chapter6.lesson7

import java.util.concurrent.atomic.AtomicLong

// 为了避免这样的问题，Java引入了原子变量。
// 当一个线程正在操作一个原子变量时，即使其他线程也想要操作这个变量，类的实现中含有一个检查那步骤操作是否完成的机制。
// 基本上，操作获取变量的值，改变本地变量值，然后尝试以新值代替旧值。
// 如果旧值还是一样，那么就改变它。
// 如果不一样，方法再次开始操作。
// 这个操作称为 Compare and Set（校对注：简称CAS，比较并交换的意思）。

private class Account{
    private val balance = AtomicLong()

    fun getBalance():Long{
        return balance.get()
    }

    fun setBalance(amount:Long){
        balance.set(amount)
    }

    fun addAmount(amount:Long){
        balance.getAndAdd(amount)
    }

    fun subtractAmount(amount: Long){
        balance.getAndAdd(-amount)
    }
}

private class Company(private val account:Account) : Runnable{

    override fun run() {
        for(index in 0 until 10){
            account.addAmount(1000)
        }
    }
}

private class Bank(private val account:Account) : Runnable{
    override fun run() {
        for(index in 0 until 10){
            account.subtractAmount(1000)
        }
    }
}

fun main(args: Array<String>) {
    val account = Account()
    account.setBalance(1000)

    val companyThread = Thread(Company(account))
    val bankThread = Thread(Bank(account))

    println("Account:Initial Balance:${account.getBalance()}")

    companyThread.start()
    bankThread.start()

    companyThread.join()
    bankThread.join()

    println("Account:Final Balance:${account.getBalance()}")
}

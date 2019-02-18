package chapter2.lesson2

class Cinema {

    var vacanciesCinema1:Long = 20L
    var vacanciesCinema2:Long = 20L

    private val controlcinema1:Any = Any()
    private val controlcinema2:Any = Any()

    // 电影院1售票
    fun sellTickets1(number:Int):Boolean{
        return synchronized(controlcinema1){
            if(number <= vacanciesCinema1){
                vacanciesCinema1 -= number
                true
            }else{
                false
            }
        }
    }

    // 电影院2售票
    fun sellTickets2(number:Int):Boolean{
        return synchronized(controlcinema2){
            if(number <= vacanciesCinema2){
                vacanciesCinema2 -= number
                true
            }else{
                false
            }
        }
    }

    // 电影院1退票
    fun returnTickets1(number:Int):Boolean{
        return synchronized(controlcinema1){
            vacanciesCinema1 += number
            true
        }
    }

    // 电影院2退票
    fun returnTickets2(number:Int):Boolean{
        return synchronized(controlcinema2){
            vacanciesCinema2 += number
            true
        }
    }

}

private class TicketOffice1(private val cinema: Cinema) : Runnable{
    override fun run() {
        cinema.sellTickets1(3)
        cinema.sellTickets1(2)
        cinema.sellTickets2(2)
        cinema.returnTickets1(3)
        cinema.sellTickets1(5)
        cinema.sellTickets2(2)
        cinema.sellTickets2(2)
        cinema.sellTickets2(2)
    }
}

private class TicketOffice2(private val cinema: Cinema) : Runnable{
    override fun run() {
        cinema.sellTickets2(2)
        cinema.sellTickets2(4)
        cinema.sellTickets1(2)
        cinema.sellTickets1(1)
        cinema.returnTickets2(2)
        cinema.sellTickets1(3)
        cinema.sellTickets2(2)
        cinema.sellTickets1(2)
    }
}

fun main(args: Array<String>) {
    val cinema = Cinema()
    val office1 = TicketOffice1(cinema)
    val thread1 = Thread(office1,"TicketOffice1")
    val office2 = TicketOffice2(cinema)
    val thread2 = Thread(office2,"TicketOffice2")

    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()

    println("Room 1 Vacancies:${cinema.vacanciesCinema1}")
    println("Room 2 Vacancies:${cinema.vacanciesCinema2}")
}
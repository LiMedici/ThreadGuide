package extentions

@Throws(InterruptedException::class)
fun Any.waitK(){
    if(this is java.lang.Object){
        this.wait()
    }
}

@Throws(InterruptedException::class)
fun Any.waitK(timeout:Long){
    if(this is java.lang.Object){
        this.wait(timeout)
    }
}

fun Any.notifyK(){
    if(this is java.lang.Object){
        this.notify()
    }
}

fun Any.notifyAllK(){
    if(this is java.lang.Object){
        this.notifyAll()
    }
}
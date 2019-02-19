package extentions

@Throws(InterruptedException::class)
fun Any.waitK(){
    if(this is java.lang.Object){
        this.wait()
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
package chapter6.lesson5

import java.util.concurrent.ConcurrentSkipListMap

// headMap(K toKey)：K是参数化ConcurrentSkipListMap对象的Key值的类。返回此映射的部分视图，其键值小于 toKey。
// tailMap(K fromKey)：K是参数化ConcurrentSkipListMap对象的Key值的类。返回此映射的部分视图，其键大于等于 fromKey。
// putIfAbsent(K key, V Value)：如果key不存在map中，则这个方法插入指定的key和value。
// pollLastEntry()：这个方法返回并删除map中最后一个元素的Map.Entry对象。
// replace(K key, V Value)：如果这个key存在map中，则这个方法将指定key的value替换成新的value。

private data class Contact(var name:String,var phone:String)

private class Task(private val id:String,private val map:ConcurrentSkipListMap<String,Contact>) : Runnable{
    override fun run() {
        for(index in 0 until 1000){
            val contact = Contact(id,(index + 1000).toString())
            map[id + contact.phone] = contact
        }
    }
}

fun main(args: Array<String>) {
    val map = ConcurrentSkipListMap<String,Contact>()
    val threads = arrayOfNulls<Thread>(25)
    for((counter, char) in ('A' until  'Z').withIndex()){
        val task = Task(char.toString(),map)
        threads[counter] = Thread(task)
        threads[counter]?.start()
    }

    threads.forEach { it?.join() }

    println("Main:Size of the map ${map.size}")
    var element  = map.firstEntry()
    var contact = element.value
    println("Main:First Entry:${contact.name}:${contact.phone}")

    element = map.lastEntry()
    contact = element.value
    println("Main:Last Entry:${contact.name}:${contact.phone}")

    println("Main:SubMap from A1996 to B1002")
    val subMap = map.subMap("A1996","B1002")
    do{
        element = subMap.pollFirstEntry()
        if(element != null){
            contact = element.value
            println("${contact.name}:${contact.phone}")
        }
    }while (element!=null)
}
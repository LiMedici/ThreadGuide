package chapter4.lesson4

import java.util.ArrayList
import java.util.concurrent.*
import kotlin.random.Random

private class UserValidator(private val name: String) {

    fun validate(name: String, password: String): Boolean {
        val random = java.util.Random()
        val duration = (Math.random() * 10).toLong()
        println("Validator : $name Validating a user during $duration seconds.")
        TimeUnit.SECONDS.sleep(duration)

        return random.nextBoolean()
    }

    fun getName(): String = name


}

private class TaskValidator(
    private val validator: UserValidator,
    private val user: String,
    private val password: String
) : Callable<String> {
    override fun call(): String {
        if (!validator.validate(user, password)) {
            println("${validator.getName()} : The user has not been found")
            throw Exception("Error validating user")
        }

        println("${validator.getName()} : The user has been found")
        return validator.getName()
    }
}

fun main(args: Array<String>) {
    val username = "test"
    val password = "test"

    val ldapValidator = UserValidator("LDAP")
    val dbValidator = UserValidator("DataBase")

    val ldapTask = TaskValidator(ldapValidator, username, password)
    val dbTask = TaskValidator(dbValidator, username, password)

    val taskList = ArrayList<TaskValidator>()
    taskList.add(ldapTask)
    taskList.add(dbTask)

    val executor = Executors.newCachedThreadPool() as ThreadPoolExecutor
    try {
        // 两个任务都返回ture。invokeAny()方法的结果是第一个完成任务的名称。
        // 第一个任务返回true，第二个任务抛出异常。invokeAny()方法的结果是第一个任务的名称。
        // 第一个任务抛出异常，第二个任务返回true。invokeAny()方法的结果是第二个任务的名称。
        // 两个任务都抛出异常。在本例中，invokeAny()方法抛出一个ExecutionException异常。
        val result = executor.invokeAny(taskList)
        println("Main:Result:$result")
    } catch (e: ExecutionException) {
        e.printStackTrace()
    }

    executor.shutdown()
    println("Main:End of the Execution")

}
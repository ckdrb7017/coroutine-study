package chapter01

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun Chapter01_Suspend() = runBlocking {
//    measureRandomTime()

    try {
        doSomething()
    }catch (e: IllegalStateException) {
        println("Chapter01_Suspend is Cancelled ${e}")
    }
}
suspend fun measureRandomTime() = coroutineScope{
    /**
     * 아래의 수행 결과는 총 2초가 걸린다.
     * 만약 getRandom1()과 getRandom2()가 동시에 수행된다면 1초에 끝나게 된다.
     *
     * async를 사용하는 방법이 있다.
     * launch를 수행할수도 있으나 결과를 받을 수 있는 것은 async이기 때문에 async가 최적의 조건이다.
     *
     * 결과가 필요 없다면 launch, 결과값이 필요하다면 async
     *
     * await을 만나면 async 블록이 끝났는지 판단하고 안끝났다면 다시 suspend 되었다 나중에 다시 깨어난다.
     *
     * CoroutineStart.LAZY 를 통해 launch, async 를 원하는 시점에 시작할 수 있다.
     */
    val elapsedTIme = measureTimeMillis {
        val randomNum1 = async(start = CoroutineStart.LAZY) { getRandom1() }
        val randomNum2 = async(start = CoroutineStart.LAZY) { getRandom2() }

        randomNum1.start()
        randomNum2.start()
        println("${randomNum1.await()} + ${randomNum2.await()} = ${randomNum1.await() + randomNum2.await()}")
    }
    println(elapsedTIme)
}

suspend fun getRandom1(): Int{
    delay(1000)
    return Random.nextInt(0, 500)
}
suspend fun getRandom2(): Int{
    delay(1000)
    return Random.nextInt(0, 500)
}

suspend fun doSomething() = coroutineScope {
    /*
    * getRandomException2 에서 delay(500)후 Exception이 발생된다.
    * 코루틴에서는 Exception이 발생하면 부모와 다른 자식 코루틴들에게도 영향을 미치게 된다.
    * 그래서 getRandomException1()은 getRandomException2()의 영향을 받아 같이 취소가 된다.
    * 자식 코루틴의 Exception은 계층적으로 상위로 전달이 된다.
    * */

    val randomNum1 = async {
        getRandomException1()
    }

    val randomNum2 = async {
        getRandomException2()
    }

    try {
        println("${randomNum1.await()} + ${randomNum2.await()} = ${randomNum1.await() + randomNum2.await()}")
    }finally {
        println("doSomething is Cancelled")
    }
}

suspend fun getRandomException1(): Int{
    try {
        delay(1000)
        return Random.nextInt(0, 500)
    }finally {
        println("getRandomException1 is Cancelled")
    }
}

suspend fun getRandomException2(): Int{
    delay(500)
    throw IllegalStateException()
}
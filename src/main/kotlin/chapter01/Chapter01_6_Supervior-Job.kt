package chapter01

import kotlinx.coroutines.*
import kotlin.random.Random

fun Chapter01_SupervisorJob() = runBlocking {
//    globalScopeTest()
    //    launchDispatchers()
    //    asyncDispatchers()
    //    exceptionHandlerTest()
    //    cannotCEHOnRunBlocking()
    //    supervisorJob()
    supervisorScopeTest()
}

@OptIn(DelicateCoroutinesApi::class)
private suspend fun globalScopeTest() = coroutineScope{
    /*
    * GlobalScope는 어떤 계층에도 속하지 않는다.
    * Android 에서는 Application 의 lifeCycle을 따른다.
    *
    * */

    val job = GlobalScope.launch {
        launch {
            delay(1000)
            printRandom()
        }
    }

    delay(500)
    job.cancel()
}

private suspend fun printRandom(){
    delay(500)
    println(Random.nextInt(0, 500))
}

val ceh = CoroutineExceptionHandler { coroutineContext, exception ->
    println("Exception occured ${coroutineContext}, ${exception}")
}

private suspend fun printRandom1() {
    delay(1000)
    println(Random.nextInt(0, 500))
}

private suspend fun printRandom2() {
    delay(500)
    throw ArithmeticException()
}

private suspend fun getRandom1(): Int {
    delay(1000)
    return Random.nextInt(0, 500)
}

private suspend fun getRandom2(): Int {
    delay(500)
    throw ArithmeticException()
}

private suspend fun exceptionHandlerTest() = coroutineScope {
    /*
     * printRandom2()에서 예외가 발생해 첫 printRandom1()의 launch가 예외를 전달받아 취소된다.
     * */

    val scope = CoroutineScope(Dispatchers.IO)
    val job = scope.launch(ceh) {
        launch {
            printRandom1()
        }
        launch {
            printRandom2()
        }
    }

    job.join()
}

private suspend fun cannotCEHOnRunBlocking() = runBlocking {
    /*
    * runBlocking 에서는 CoroutineExceptionHandler가 동작하지 않는다.
    * */
    val job = launch(ceh) {
        val a = async {
            getRandom1()
        }

        val b = async {
            getRandom2()
        }

        println(a.await())
        println(b.await())
    }
    job.join()
}

private suspend fun supervisorJob() = runBlocking {
    /*
    * 자식 코루틴에서 예외가 발생하면 예외는 부모까지 전달이 된다.
    * 부모에서 예외가 났기 때문에 다른 자식들도 영향을 미친다.
    * 이때 SupervisorJob 을 이용하면 예외를 아래쪽으로만 전달한다.
    * */
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + ceh)
    val job1 = scope.launch { printRandom1() }
    val job2 = scope.launch { printRandom2() }

    joinAll(job1, job2)
}

private suspend fun supervisorScopeTest() = supervisorScope {
    /*
    * SupervisorJob() 과 CoroutineScope을 합친 scope빌더 이다.
    * 이 빌더를 쓰는 곳은 예외가 발생할 곳에 CoroutineExceptionHandler 을 넣어줘야 한다.
    * */

    launch { printRandom1() }
    launch(ceh) { printRandom2() }
}
package chapter01

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun Chapter01_Context_Dispatcher() = runBlocking {
//    launchDispatchers()
//    asyncDispatchers()
//    newParentJob()
//    parentJob()
    contextCombination()
}

private suspend fun launchDispatchers() = coroutineScope {

    // 현재 부모 코루틴에서 실행되는 쓰레드로 실행
    launch {
        println("부모의 컨텍스트 / ${Thread.currentThread().name}")
    }

    // CPU 코어 수에 비례하는 스레드 풀에서 실행
    launch(Dispatchers.Default) {
        println("Default / ${Thread.currentThread().name}")
    }

    // Maximum 64개 까지 생성될 수 있는 스레드풀에서 실행,
    // Dispatchers.Default로 실행되는 부모 코루틴에서 자식 코루틴이 withContext(Dispatchers.IO)를 사용할 때
    // 효율을 높일 수 있다. Dispatchers.IO 내부에서 Dispatchers.Default로 실행되는 쓰레드를 공유하기 때문이다.
    // 이 때문에 쓰레드 컨텍스트 스위칭이 일어나지 않기 때문이다.
    launch(Dispatchers.IO) {
        println("IO / ${Thread.currentThread().name}")
    }

    // 실행되는 Dispatcher가 매번 달라질 수 있다.
    launch(Dispatchers.Unconfined) {
        println("Unconfined / ${Thread.currentThread().name}")
    }

    // 쓰레드풀이 아닌 매번 새로운 쓰레드를 만들어서 실행
    launch(newSingleThreadContext("Fast Campus")) {
        println("newSingleThreadContext / ${Thread.currentThread().name}")
    }
}

private suspend fun asyncDispatchers() = coroutineScope {

    async {
        println("부모의 컨텍스트 / ${Thread.currentThread().name}")
    }

    async(Dispatchers.IO) {
        println("Default / ${Thread.currentThread().name}")
    }

    async(Dispatchers.IO) {
        println("IO / ${Thread.currentThread().name}")
    }

    // delay를 주니 실행되는 쓰레드가 바뀌었다.
    // 중단점을 만나면 Dispatcher가 바뀌어 불확실한 상황이 만들어지기 때문에 사용을 추천하지 않는다.
    async(Dispatchers.Unconfined) {
        println("Unconfined / ${Thread.currentThread().name}")
        delay(500)
        println("Unconfined / ${Thread.currentThread().name}")
    }

    async(newSingleThreadContext("Fast Campus")) {
        println("newSingleThreadContext / ${Thread.currentThread().name}")
    }
}


private suspend fun newParentJob() = coroutineScope {
    /*
    * 코루틴 스코프, 코루틴 컨텍스트는 계층적으로 되어있다.
    * 아래 첫번쨰 launch 는 parentJob()의 coroutineScope를 부모로 갖고 있다.
    * 2번째 launch는 Job()으로 새로운 부모를 생성해서 넣어주므로 상위의 launch는 부모가 아니다.
    * 3번째 launch는 새로운 부모를 넣어주지 않으므로 상위의 launch가 부모가 된다.
    *
    * 취소가 될때 Job() 을 넣은 launch는 취소가 되지 않는다. 부모-자식 관계가 끊어지는 것으로 이해하면 된다.
    * */
    val job = launch {
        launch(Job()) {
            println(coroutineContext[Job])
            println("launch1 : ${Thread.currentThread().name}")
            delay(1000)
            println(3)
        }

        launch {
            println(coroutineContext[Job])
            println("launch2 : ${Thread.currentThread().name}")
            delay(1000)
            println(1)
        }
    }

    delay(500)
    job.cancelAndJoin()
    delay(1000)
}

private suspend fun parentJob() = coroutineScope {
    /*
    * 첫번째 launch 가 아래 두개의 launch의 부모가 된다.
    * 부모한테 join을 걸면 자식 launch 들이 끝날때 까지 기다리게 된다.
    * 취소가 되면 아래 두 launch 모두 취소가 된다.
    * */
    val elapsed = measureTimeMillis {
        val job = launch {
            launch {
                println("launch1 : ${Thread.currentThread().name}")
                delay(5000)
            }

            launch {
                println("launch2 : ${Thread.currentThread().name}")
                delay(10)
            }
        }
        job.join()
    }

    println(elapsed)
}

@OptIn(ExperimentalStdlibApi::class)
private suspend fun contextCombination() = coroutineScope {

    /*
    * CoroutineContext는 + 연산으로 결합을 할 수 있다. 합쳐진 Context들은 coroutineContext[] 로 조회가 가능하다.
    * 아래에서 첫번째 launch 가 Context A 라고 한다면 두번째와 세번째 launch는 위 부모의 Context + 새로 생성되는 Context들의 결합이 된다.
    * 즉 2번째 Context는 Context A + Dispatchers.IO + CoroutineName("launch1") 이 되는 것이다.
    *
    * */
    launch {
        launch(Dispatchers.IO + CoroutineName("launch1")) {
            println("launch1 : ${Thread.currentThread().name}")
            println(coroutineContext[CoroutineDispatcher])
            println(coroutineContext[CoroutineName])
            delay(5000)
        }

        launch(Dispatchers.Default + CoroutineName("launch2")) {
            println("launch2 : ${Thread.currentThread().name}")
            println(coroutineContext[CoroutineDispatcher])
            println(coroutineContext[CoroutineName])
            delay(10)
        }
    }
}
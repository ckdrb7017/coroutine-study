package chapter01

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis

fun Chapter01_Mutex_Actor() = runBlocking {
    testMassiveRun()
}

var count = 0
//var count = AtomicInteger()

var counterContext = newSingleThreadContext("CounterContext")
private suspend fun testMassiveRun() = runBlocking {

    /*
    * withContext는 블록의 코드가 수행이 완료될떄 까지 기다리는 코루틴 빌더이다.
    * 마지막 결과값을 반환할 수 있으며 async{}.await()처럼 동작한다.
    * coroutineScope처럼 중단점을 갖고 먼저 시작하게 된다.
    *
    *
    * */

    // Dispatcher.Default or IO 일 경우 공유자원 문제 발생
    withContext(counterContext) {
        massiveRun {
            count++
//            count.incrementAndGet()
        }
    }

    println("count = ${count}")
}

suspend fun massiveRun(action: suspend () -> Unit) {
    /*
    * 100개의 launch에서 각 1000번을 실행하여 count를 올린다.
    * 이때 공유자원 문제가 생길 수 있다.
    * launch1을 실행하는 A 쓰레드와 launch2을 실행하는 B 쓰레드가
    * count == 0 일때 같이 읽고 쓰면 +2 가 되어야 하는 count가 각 0->1, 0->1 로 쓰여
    * count == 1 이 되는 것이다.
    * 해결 방법은
    * 첫째로 AtomicInteger 를 사용하는 것이다. 그러나 항상 정답은 아니다.
    * 두번째는 실행하는 쓰레드를 한개로 특정하여 구현하는 것이다.
    *
    * 상위에서 실행되는 withContext 가 Dispatcher.Default이면 여러 쓰레드에서 동작하기 때문에
    * newSingleThreadContext 를 통해 하나의 쓰레드를 만들어 실행하는 것이다.
    * */

    val n = 100
    val k = 1000
    val elapsed = measureTimeMillis {
        coroutineScope {
            repeat(n) {
                launch {
                    repeat(k) {
                        action()
                    }
                }
            }
        }
    }

    println("${elapsed} ms동안 ${n * k}개의 액션을 수행했습니다.")
}
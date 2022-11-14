import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun Chapter01_ScopeBuilder() = runBlocking {

    Chapter01_ScopeBuilder_CoroutineContext()

//    Chapter01_ScopeBuilder_delay_sleep()

//    Chapter01_ScopeBuilder_delay_sleep_example()

//    Chapter01_ScopeBuilder_Structured_Concurrency_example()
}

fun CoroutineScope.Chapter01_ScopeBuilder_CoroutineContext() {
    /*
* runBlocking은 해당 코드 블럭이 실행이 모두 끝날때 까지
* runBlocking 이후의 코드를 수행하지 못하는 특징을 갖는다.
* 내부적으로 BlockingCoroutine을 사용하고있다.
*
* 모든 코루틴의 시작은 코루틴 스코프로 부터 시작된다.
* 코루틴 스코프는 CoroutineContext를 갖고 있는데 이 Context는 코루틴을 처리하기 위한 정보들이 들어있다.
* 코루틴 아이디, 활성상태 등
* */
    println(this)
    println(coroutineContext)
    println(Thread.currentThread().name)
    println("Hello")
}

suspend fun CoroutineScope.Chapter01_ScopeBuilder_delay_sleep() {
    /*
* launch 빌더는 다른 코루틴 코드를 같이 수행시키는 빌더이다.
* 여거시 runBlocking의 결과가 먼저 나오는데 runBlocking 코드가
* 먼저 메세지큐에 들어아고 launch 빌더가 이후에 들어가기 때문이다.
*
* delay()를 사용하면 해당 코드를 실행하는 쓰레드를 블럭시키지 않고 다른 코루틴을 실행한다.
* 반면 sleep()은 해당 쓰레드를 블럭시킨다.
*
* */
    launch {
        println("launch : ${Thread.currentThread().name}")
        println("World!")
    }

    println("runBlocking : ${Thread.currentThread().name}")
//    Thread.sleep(1000)
    delay(1000)
    println("Hello")
}

suspend fun CoroutineScope.Chapter01_ScopeBuilder_delay_sleep_example() {
    /*
    * 동작하는 순서는 runBlocking -> launch01 -> launch02 순으로 실행된다.
    * runBlocking에서는 delay(500)이 걸려있어 launch01 로 실행이 옮겨가고
    * launch01에서는 delay(1000)이 걸려있어 launch02 로 실행이 옮겨가고
    * launch02는 바로 println(1)을 호출한다.
    * 이후 delay(500)이 완료돼 println(2)을 호출하고 마지막으로 println(3)을 호출한다.
    *
    * 이것이 가능한 이유는 delay()가 suspend 함수(중단점을 갖도록 하는)이기 때문이다.
    * 그래서 해당 부분에서 중단점을 갖고 다시 돌아왔을때 재실행하여 이후 코드를 실행할 수 있다.
    * */

    launch {
        println("launch01: ${Thread.currentThread().name}")
        delay(1000)
        println(3)
    }
    launch {
        println("launch02: ${Thread.currentThread().name}")
        println(1)
    }
    println("runBlocking : ${Thread.currentThread().name}")
    delay(500)
    println(2)
}

fun CoroutineScope.Chapter01_ScopeBuilder_Structured_Concurrency_example() {
    /*
    * 코루틴은 구조적 동시성을 갖는다.
    * 즉 부모코루틴은 자식코루틴이 끝날때 까지 끝나지 않는다는 특징을 갖는다.
    * 이 때문에 runBlocking이 끝난 이후에 println(4)가 호출이 된다.
    * 또한 부모코루틴이 취소돼면 자식코루틴도 같이 취소가 된다.
    * */
    runBlocking {
        launch {
            println("launch01: ${Thread.currentThread().name}")
            delay(1000)
            println(3)
        }
        launch {
            println("launch02: ${Thread.currentThread().name}")
            println(1)
        }
        println("runBlocking : ${Thread.currentThread().name}")
        delay(500)
        println(2)
    }
    println(4)
}
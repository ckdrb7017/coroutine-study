package chapter02

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

fun Chapter02_Flow_Basic() = runBlocking {

    /*
    * Flow란?
    * 코틀린에서 쓸수있는 코루틴으로 이루어진 비동기 스트림이다.
    *
    * flow빌더 함수를 이용해 코드블록을 구성하고 emit을 호출해 스트림에 데이터를 방출
    * flow는 기본적으로 콜드 스트림이기 때문에 요청측에서 collect를 호출해야 값을 발생
    * 콜드 스트림 : 요청이 있는 경우(collect) 1:1로 값을 전달
    * 핫 스트림 : 0개 이상의 요청자에게 값을 전달
    * */


    /*  기본적인 사용
      flowSomething().collect { value ->
            println(value)
        }
    */

    // Flow 취소하기
    val result = withTimeoutOrNull(500L) {
        flowSomething().collect { value ->
            println(value)
        }
        true
    } ?: false

    if (result.not()) {
        println("Flow가 취소됐습니다")
    }
}

fun flowSomething(): Flow<Int> = flow {
    repeat(10) {
        emit(Random.nextInt(0, 500))
        delay(100)
    }
}

suspend fun flowOfBuilder() {
    flowOf(1, 2, 3, 4, 5).collect {
        println(it)
    }
}

suspend fun asFlowBuilder() {
    listOf(1, 2, 3, 4, 5).asFlow().collect {
        println(it)
    }

    (6..10).asFlow().collect {
        println(it)
    }
}
package chapter01

import kotlinx.coroutines.*

fun Chapter01_Job_Concurrency() = runBlocking {
    doOneTwoThree()
}

/*
* 아래 함수는 실행이 되지 않는다.
* launch는 coroutine 빌더 안에서 실행이 되어야 하는데
* coroutine빌더가 없기 때문이다.
* suspend 키워드는 함수에 suspension point를 만들 뿐, 코루틴 스코프가 아니다.
*  = coroutineScope{} 같이 expression body로 표현하면 아래 launch를 실행할 수 있다.
* */
suspend fun doOneTwoThree() {
    launch {
        println("launch01: ${Thread.currentThread().name}")
        delay(1000)
        println(3)
    }
    launch {
        println("launch01: ${Thread.currentThread().name}")
        println(1)
    }
    launch {
        println("launch01: ${Thread.currentThread().name}")
        delay(500)
        println(2)
    }

    println(4)
}
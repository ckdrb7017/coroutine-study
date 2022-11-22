package chapter02

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun Chapter02_Flow_Flattening() = runBlocking<Unit> {
    flatMapTest()
}

/*
* Flow에는 3가지 유형의 flatMap을 지원
*
* flatMapConcat,
* flatMapMerge,
* flatMapLatest
* */

suspend fun flatMapTest() {
    val startTime = System.currentTimeMillis()
    /*
    * onEach에 호출하면  emit("$i First") 만 호출되고 끝나버린다.
    * flatMapConcat 을 이용하면 emit("$i First")와 emit("$i Second") 이 끝나고 난 뒤에 다음 flow가 수행된다.
    * flatMapConcat은 결과를 이어서 붙인다.
    *
    * flatMapConcat 결과
    * 1 First -> 1 Second -> 2 First -> 2 Second -> 3 First -> 3 Second
    *
    * ////////////////////////////////////////////////////////////////////////
    *
    * flatMapMerge는 첫 요소 시작 후 다음 요소의 flattening을 시작
    *
    * flatMapConcat 결과
    * 1 First -> 2 First -> 3 First -> 1 Second -> 2 Second -> 3 Second
    *
    * ////////////////////////////////////////////////////////////////////////
    *
    * flatMapLatest 는 다음 요소의 flattening을 시작하며 이전에 진행중인 것을 취소
    * requestFlow(1) 시작 - 취소, requestFlow(2) 시작 - 취소, requestFlow(3) 시작
    *
    * flatMapLatest 결과
    * 1 First -> 2 First -> 3 First -> 3 Second
    * */

    (1..3).asFlow()
        .onEach { delay(100) }
        .flatMapLatest { requestFlow(it) }
        .collect {
            println("${it} at ${System.currentTimeMillis() - startTime}ms")
        }
}

fun requestFlow(i: Int): Flow<String> = flow {
    emit("$i First")
    delay(500)
    emit("$i Second")
}
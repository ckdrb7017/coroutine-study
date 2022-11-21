package chapter02

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun Chapter02_Flow_Buffer() = runBlocking<Unit> {
    val measureTime = measureTimeMillis {
        simple()
            //.buffer() //buffer를 이용하면 sender가 기다리지 않고 보내게 된다.
            //.conflate() // 중간 값들을 누락하고 마지막 결과를 처리
            //.collectLatest //  conflate는 기존에 실행중인건 유지하지만, collectLatest는 진행중일때 새로운 데이터가 들어오면 기존것을 취소하고 새로운 것을 실행
            .collect {
            delay(300)
            println(it)
        }
    }
    println("Collected in $measureTime ms")
}

private fun simple() = flow<Int> {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}
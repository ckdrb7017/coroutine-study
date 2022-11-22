package chapter02

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun Chapter02_Flow_Combine() = runBlocking<Unit> {
//        zipFlow()
        combineFlow()
}

/*
* zip은 두 데이터가 동시에 준비돼야 동작
*
* */
private suspend fun zipFlow() {
    val nums = (1..3).asFlow()
    val strs = flowOf("일", "이", "삼")

    nums.zip(strs) { a, b ->
        "${a} is ${b}"
    }.collect {
        println(it)
    }
}

/*
* combine은 한쪽이 준비되면 동작
* */
private suspend fun combineFlow() {
    val nums = (1..3).asFlow().onEach { delay(100) }
    val strs = flowOf("일", "이", "삼").onEach { delay(200) }

    nums.combine(strs) { a, b ->
        "${a} is ${b}"
    }.collect {
        println(it)
    }
}

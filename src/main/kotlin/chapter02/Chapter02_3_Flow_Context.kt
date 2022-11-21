package chapter02

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

fun Chapter02_Flow_Context() = runBlocking<Unit> {
    /*
    * Flow는 기본적으로 현재 코루틴 컨텍스트에서 호출
    * */

//    launch(Dispatchers.IO) {
////        simple().collect{
////            testLog("$it 를 받음")
////        }
//
//    }

    simpleWithContext().collect {
        testLog("Main $it 를 받음")
    }
}

private fun testLog(msg: String) {
    println("[${Thread.currentThread().name}] $msg")
}

private fun simple() = flow<Int> {
    testLog("flow 시작")

    for (i in 1..10) {
        emit(i)
    }
}

private fun simpleWithContext() = flow<Int> {
    /*
    * Flow 내에서는 Context를 바꿀수 없다.
    * Context를 바꾸기 위해선 flowOn을 이용해 Context를 바꿀 수 있다.
    *
    * flowOn은 UpStream에 있는 연산들을 원하는 Context로 변경하여 수행할 수 있다.
    * UpStream : flowOn 코드가 실행되기 이전까지의 연산들
    * DownStream : flowOn 코드 이후의 연산들
    * */

//    withContext(Dispatchers.Default){
//        for(i in 1..10){
//            delay(100)
//            emit(i)
//        }
//    }
    for (i in 1..10) {
        delay(100)
        testLog("값 ${i}를 emit")
        emit(i)
    }
}
    .map { it * 1 } // UpStream
    .flowOn(Dispatchers.Default)
    .map { it * 2 } // DownStream
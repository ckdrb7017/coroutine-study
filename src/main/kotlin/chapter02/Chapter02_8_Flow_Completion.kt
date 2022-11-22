package chapter02

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun Chapter02_Flow_Completion() = runBlocking<Unit> {

//    testCompletionTryCatch()
    testCompletionOnComplete()
}

private fun simple(): Flow<Int> = (1..3).asFlow()

private suspend fun testCompletionTryCatch() {
    /*
    * 완료를 처리하는 방법중 하나는 finally 를 이용하는 것이다.
    * */
    try {
        simple().collect {
            println(it)
        }
    } finally {
        println("Done")
    }
}

private suspend fun testCompletionOnComplete() {
    /*
    * 완료를 처리하는 방법중 하나는 onCompletion 를 이용하는 것이다.
    *
    * onCompletion을 사용하면 종료처리를 할때 예외가 발생됐는지 알 수 있다.
    * 즉 정상적인 종료와 에러가 일어난 후의 종료를 처리할 수 있는 것이다.
    * */
    simple().map {
        if (it > 2) {
            throw IllegalStateException()
        }
        it + 1
    }
        .onCompletion { e ->
            if(e != null){
                println("Exception occured")
            }
            println("Done")
        }
        .catch { e -> println("Catch") }
        .collect {
            println(it)
        }
}

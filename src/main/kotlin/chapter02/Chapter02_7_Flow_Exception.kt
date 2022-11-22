package chapter02

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun Chapter02_Flow_Exception() = runBlocking<Unit> {
    handleException()
}


private suspend fun handleException(){
    /*
    * 예외를 처리하는 첫번째 방법은 try-catch를 사용하는 방법이다.
    *
    * */
//    try {
//        simple().collect{
//            println(it)
//        }
//    }catch (e: Exception){
//        println("Catch ${e.message}")
//    }

    /*
    * 두번째는 catch{}블록을 이용하여 처리하는 것이다.
    * catch는 Upstream에만 영향을 받고 DownStream에서 일어나는 것은 영향을 안받는다.
    * */
    simple()
        .catch { e -> emit("Catch ${e.message}") }
        .collect{
        println(it)
    }
}

private fun simple(): Flow<String> = flow {
    for(i in 1..3){
        println("Emitting $i")
        emit(i)
    }
}.map {
    check(it <= 1){
        "Collected $it"
    }
    "String ${it}"
}
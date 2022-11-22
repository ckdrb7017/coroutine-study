package chapter02

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

fun Chapter02_Flow_Launching() = runBlocking<Unit> {

//    testOnEach()
    testLaunchIn()
}

private fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(5000) }

private fun log(msg: String) = println("$msg")

private suspend fun testOnEach() {
    /*
    * onEach를 통해 스트림이 UpStream -> DownStream으로 이동 할 때 이벤트를 처리할 수 있다.
    * 다만 이때 문제점은 collect() 가 늦게 호출될 수 있다.
    * collect는 이전의 스트림들이 끝나야 실행된다.
    * event가 지속적으로 발생하는 ui나 io 작업은 같이 진행되기 힘들다.
    * */
    events()
        .onEach {
            println("Event : ${it}")
        }
        .collect {
        }
    println("Done")
}

private fun CoroutineScope.testLaunchIn() {

    /*
    * launchIn을 이용하면 별도의 코루틴에서 플로우를 실행
    * 내부적으로는
    * scope.launch { flow.collect() }
    * 이렇게 별도의 코루틴에서 실행된다.
    * onEach로 인해 무한정 기다리지 않게 하기 위함이다.
    * */
    events()
        .onEach {
            println("Event : ${it}")
        }
        .launchIn(this)

}


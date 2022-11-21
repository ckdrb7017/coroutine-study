package chapter02

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun Chapter02_Flow_Operation() = runBlocking {

//    mapOperationFlow()
//    filterOperationFlow()
//    transformOperationFlow()
//    takeOperationFlow()
//    takeWhileOperationFlow()
//    dropOperationFlow()
//    reduceOperationFlow()
    foldOperationFlow()
}

private fun flowSomething(): Flow<Int> = flow {
    repeat(10) {
        emit(Random.nextInt(0, 500))
        delay(10)
    }
}

private suspend fun mapOperationFlow() {
    flowSomething().map {
        "$it $it"
    }.collect {
        println(it)
    }
}

private suspend fun filterOperationFlow() {
    (1..20).asFlow().filter {
        (it % 2) == 0
    }.collect {
        println(it)
    }
}

private suspend fun transformOperationFlow() {
    (1..20).asFlow().transform {
        emit(it)
        emit(someCalc(it))
    }.collect {
        println(it)
    }
}

private suspend fun takeOperationFlow() {
    (1..20).asFlow().transform {
        emit(it)
        emit(someCalc(it))
    }
        .take(5)
        .collect {
            println(it)
        }
}

/*
* takeWhile 은 특정 조건에 만족하는 것만 가져온다.
* 만약 조건이 깨지면 즉시 종료된다.
* it % 2 == 1 인 경우 한번만 호출
* */
private suspend fun takeWhileOperationFlow() {
    (1..20).asFlow().transform {
        emit(it)
        emit(someCalc(it))
    }.takeWhile {
        it < 15
    }.collect {
        println(it)
    }
}

/*
* drop은 n개의 결과를 버리고 시작
* */
private suspend fun dropOperationFlow() {
    (1..20).asFlow().transform {
        emit(it)
        emit(someCalc(it))
    }
        .drop(5)
        .collect {
            println(it)
        }
}

/*
* reduce 는 각 단계마다 누적된 결과를 가져와 축적하면서 계산되는 함수
*
* */
private suspend fun reduceOperationFlow() {
    val result = (1..10).asFlow().reduce {
            accumulator, value ->
        accumulator + value
    }

    println(result)
}

/*
* fold 는 reduce와 비슷하지만 초기값이 있다는 차이만 존재
*
* */
private suspend fun foldOperationFlow() {
    val result = (1..10).asFlow().fold(10) {
            accumulator, value ->
        accumulator + value
    }

    println(result)
}


private suspend fun someCalc(i: Int): Int {
    delay(100)
    return i * 2
}
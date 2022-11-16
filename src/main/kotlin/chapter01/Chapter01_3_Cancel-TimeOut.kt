package chapter01

import kotlinx.coroutines.*

fun Chapter01_Cancel_TimeOut() = runBlocking {
//    doOneTwoThree()
//    doCount()
//    doOneTwoThreeTryCatch()
//    doOneTwoThreeNonCancellable()
    doCountTimeOut()
}

private suspend fun doOneTwoThree() = coroutineScope {

    /*
    * cancel()을 사용하면 해당 job을 취소할 수 있다.
    * 아래 코드는 800ms 이후에 job1~3 을 취소하는데
    * delay()에서 suspension point가 걸려 위에 있는 job1의 launch를 실행
    * job1의 delay(1000) 에 걸려 job2 를 실행
    * delay()가 없으므로 모두 완료하고
    * job3을 실행한다.
    * 이후 delay(500)을 지나 println(2) 를 호출한다.
    * 이후 800ms 에서 job1~3을 취소하기 때문에
    * job1은 delay(1000) 이후 코드를 실행하지 못하고 취소된다.
    *
    * */
    val job1 = launch {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000)
        println(3)
    }

    val job2 = launch {
        println("launch2: ${Thread.currentThread().name}")
        println(1)
    }

    val job3 = launch {
        println("launch3: ${Thread.currentThread().name}")
        delay(500)
        println(2)
    }

    delay(800)
    job1.cancel()
    job2.cancel()
    job3.cancel()
    println(4)
}

suspend fun doCount() = coroutineScope {
    /*
    * 아래 코드는 cancle()이 불가능한 Job인 경우이다.
    * 여기서는 크게 2가지 문제가 있다
    * 첫번째는 job1이 다 끝난 후에 doCount Done을 출력
    * 두번째는 cancel()이 호출되지 않는다는 것이다.
    * */

    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L
        // 두번째 문제를 해결하기 위해 && isActive 조건을 while에 추가,
        // while문에서 코루틴의 상태를 모르기 때문에 cancel을 해도 취소가 안됐다.
        // 코루틴에서 cancel() 실행되면 resume될때 JobCancellationException을 발생시켜 코루틴을 취소하게 된다.
        while (i <= 10) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }

    delay(200)
    // 첫번째 문제를 해결하기 위해 join()을 사용, job1의 작업이 완료된 이후에 println("doCount Done") 실행
    job1.cancel()
    job1.join()
    //job1.cancelAndJoin()
    println("doCount Done")
}

private suspend fun doOneTwoThreeTryCatch() = coroutineScope {
    /*
    * suspend 함수들은 JobCancellationException을 발생시켜 try-catch-finally로 대응할 수 있다.
    * job1 은 작업을 다 완료하지 못해 delay(800) 이후 취소가 되기 떄문에 JobCancellationException이 발생한다.
    * job2와 job3은 delay(800) 이전에 작업을 모두 완료하여 cancel()을 호출해도 영향이 없다.
    *  */
    val job1 = launch {
        try {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000)
            println(3)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            println("job1 finished")
        }
    }

    val job2 = launch {
        try {
            println("launch2: ${Thread.currentThread().name}")
            println(1)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            println("job2 finished")
        }
    }

    val job3 = launch {
        try {
            println("launch3: ${Thread.currentThread().name}")
            delay(500)
            println(2)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            println("job3 finished")
        }
    }

    delay(800)
    job1.cancel()
    job2.cancel()
    job3.cancel()
    println(4)
}

private suspend fun doOneTwoThreeNonCancellable() = coroutineScope {

    /*
    * job을 cancel() 할 때 반드시 실행해야 되는 구문이 있다.
    * 이때 withContext(NonCancellable) 을 이용하면 해결할 수 있다.
    * */
    val job1 = launch {
        withContext(NonCancellable) {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000)
            println(3)
        }
        delay(1000)
        println("job1 end")
    }

    val job2 = launch {
        withContext(NonCancellable) {
            println("launch2: ${Thread.currentThread().name}")
            delay(1000)
            println(1)
        }
        delay(1000)
        println("job2 end")
    }

    val job3 = launch {
        withContext(NonCancellable) {
            println("launch3: ${Thread.currentThread().name}")
            delay(1000)
            println(2)
        }
        delay(1000)
        println("job3 end")
    }

    delay(800)
    job1.cancel()
    job2.cancel()
    job3.cancel()
    println(4)
}

private suspend fun doCountTimeOut() = coroutineScope {
    /*
    * withTimeout을 통해 일정시간이 끝난 후에 코루틴을 종료할 수 있다.
    * withTimeout을 사용하면 try-catch로 에러를 핸들링할수 있지만 매우 불편한 작업이다.
    * 그래서 withTimeoutOrNull을 사용하여 실패했을때 null을 반환하고 이것으로 처리를 할 수 있다.
    * */

    val result = withTimeoutOrNull(500) {
        val job1 = launch(Dispatchers.Default) {
            var i = 1
            var nextTime = System.currentTimeMillis() + 100L
            while (i <= 10 && isActive) {
                val currentTime = System.currentTimeMillis()
                if (currentTime >= nextTime) {
                    println(i)
                    nextTime = currentTime + 100L
                    i++
                }
            }
        }
        true
    } ?: false

    println(result)
}
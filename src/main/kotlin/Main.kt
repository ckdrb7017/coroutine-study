import chapter01.Chapter01_Job_Concurrency

fun main() {
    Chapter01_ScopeBuilder()
    Chapter01_Job_Concurrency()
}

fun printTitle(title: String = "") {
    println(title)
    println()
}

fun printDivider() {
    println("------------------------------------------------------------------------------")
}
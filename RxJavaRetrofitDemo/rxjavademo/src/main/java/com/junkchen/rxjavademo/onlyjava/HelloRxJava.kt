package com.junkchen.rxjavademo.onlyjava

import io.reactivex.Observable
import java.util.function.Consumer

fun main(args: Array<String>) {
    hello(arrayOf("RxJava", "Kotlin", "Python", "Java"))
}

private fun hello(names: Array<String>) {
//    for (name in names) {
//        println(name)
//    }
//    Observable.fromArray(names)
//            .subscribe(object: Consumer<String> {
//                println(it)
//            })
}
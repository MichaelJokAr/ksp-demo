package com.example.jokar.ksp.spi.annoation

interface SPIServiceCallable<T> {
    fun call(): T
    fun singleton(): Boolean
    fun priority(): Int
}
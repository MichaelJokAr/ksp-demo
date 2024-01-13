package com.example.myapplication

import com.example.jokar.ksp.spi.annoation.SPIService
import com.example.jokar.ksp.spi.annoation.SPIServiceCallable
import org.jokar.my.library.a.ICommonService

object Library {
    const val tag = "1"

    @JvmStatic
    fun name(): String {
        val arrayOf: Array<Class<*>> = arrayOf(Book::class.java)
        return "222"
    }
}


data class Book(
    val name: String
)

@SPIService(priority = 1, singleton = true, services = [ICommonService::class])
class CommonImpl : ICommonService


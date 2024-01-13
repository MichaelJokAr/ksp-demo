package com.example.jokar.ksp.spi.compiler

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import java.util.logging.Logger

object LogUtil {
    private var logger: KSPLogger? = null
    fun setLogger(logger: KSPLogger) {
        this.logger = logger
    }

    fun log(value: String) {
        logger?.warn("[SPI] $value")
    }
}
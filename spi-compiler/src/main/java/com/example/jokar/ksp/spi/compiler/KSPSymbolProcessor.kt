package com.example.jokar.ksp.spi.compiler

import com.example.jokar.ksp.spi.annoation.SPIService
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate

class KSPSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        LogUtil.log("KSPSymbolProcessor")
        val symbols = resolver.getSymbolsWithAnnotation(SPIService::class.qualifiedName!!)
        val moduleNme = environment.options["module_name"].toString()
        LogUtil.log(moduleNme)
        val ret = mutableListOf<KSAnnotated>()
        symbols.toList().forEach { annotated ->
            if (!annotated.validate()) ret.add(annotated)
            else {
                annotated.accept(SPIServiceVisitor(environment, annotated.annotations), Unit)//处理符号
            }
        }
        //返回无法处理的符号
        return ret

    }
}
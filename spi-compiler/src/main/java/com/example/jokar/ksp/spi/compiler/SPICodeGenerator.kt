package com.example.jokar.ksp.spi.compiler

import com.example.jokar.ksp.spi.annoation.SPIServiceCallable
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName

object SPICodeGenerator {

    fun generator(
        packageName: String,
        className: String,
        serviceClass: KSClassDeclaration,
        serviceImplClass: KSClassDeclaration,
        singleton: Boolean,
        priority: Int,
    ): FileSpec {
        val serviceImplClassPackage = serviceImplClass.packageName.asString()
        val serviceImplClassName = serviceImplClass.simpleName.getShortName()
        LogUtil.log(
            "[generator] $className;[serviceImpl]${serviceImplClassPackage}," +
                    serviceImplClassName
        )
        val serviceImplClassClassName = ClassName(serviceImplClassPackage, serviceImplClassName)
//        val serviceClassClassName = ClassName(serviceClass.packageName.asString(),
//            serviceClass.simpleName.getShortName())
        val serviceCallable = SPIServiceCallable::class
            .asClassName()
            .plusParameter(serviceImplClassClassName)
        val serviceImplClassMember = MemberName(serviceImplClassPackage, serviceImplClassName)
        return FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.classBuilder(className)
                    .addSuperinterface(serviceCallable)
                    .addFunction(
                        FunSpec.builder("call")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(serviceImplClassClassName)
                            .addStatement("return %M()", serviceImplClassMember)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("singleton")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(Boolean::class.asClassName())
                            .addStatement("return $singleton")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("priority")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(Int::class.asClassName())
                            .addStatement("return $priority")
                            .build()
                    )
                    .build()
            )
            .build()


    }
}
package com.example.jokar.ksp.spi.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid
import java.io.OutputStream

class SPIServiceVisitor(
    private val environment: SymbolProcessorEnvironment,
    private val annotations: Sequence<KSAnnotation>
) : KSVisitorVoid() {

    private companion object {
        const val TAG = "SPIServiceVisitor"
        const val ANNOTATION_NAME_SERVICES = "services"
        const val ANNOTATION_NAME_SINGLETON = "singleton"
        const val ANNOTATION_NAME_PRIORITY = "priority"
    }

    private var services: MutableList<KSClassDeclaration> = mutableListOf()
    private var singleton: Boolean = true
    private var priority: Int = -1

    init {
        LogUtil.log("[$TAG]-init")
        getAnnotationParams()
    }

    private fun getAnnotationParams() {
        annotations.forEach { annotation ->
            annotation.arguments.forEach { argument ->
                when (argument.name?.getShortName()) {
                    ANNOTATION_NAME_SERVICES -> {
                        (argument.value as? ArrayList<*>?)?.forEach {
                            ((it as? KSType)?.declaration as? KSClassDeclaration)?.let {
                                services.add(it)
                            }
                        }
                    }

                    ANNOTATION_NAME_SINGLETON -> {
                        singleton = argument.value as? Boolean ?: true
                    }

                    ANNOTATION_NAME_PRIORITY -> {
                        priority = argument.value as? Int ?: -1
                    }
                }
            }
        }
        LogUtil.log("[annotations]${services},${singleton},${priority}")
    }


    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        LogUtil.log("[$TAG]-visitClassDeclaration: ${classDeclaration},${data}")
        classDeclaration.superTypes.forEach {
            LogUtil.log("[$TAG]-superTypes:${it.resolve().declaration.qualifiedName?.asString()}")
        }
        //2. 解析Class信息
        val qualifiedName = classDeclaration.qualifiedName?.asString()

        //获取类名
        val originalClassName = classDeclaration.simpleName.asString()
        //获取这个类的包名
        val packageName = classDeclaration.packageName.asString()


        services.forEach { ksDeclaration ->
            classDeclaration.superTypes.find {
                val superClass = it.resolve().declaration.qualifiedName?.asString()
                superClass == ksDeclaration.qualifiedName?.asString()
            }?.let {
                val callable = "${ksDeclaration.simpleName.asString()}Creator"
                val file = environment.codeGenerator.createNewFile(
                    dependencies = Dependencies(aggregating = false),
                    packageName = packageName,
                    fileName = callable
                )
                LogUtil.log("[$TAG]-[type Match]${it}")
                generateFile(
                    packageName = packageName,
                    serviceImplClass = classDeclaration,
                    serviceClass = ksDeclaration,
                    outputStream = file,
                    callable
                )
            }
        }
    }

    private fun generateFile(
        packageName: String,
        serviceImplClass: KSClassDeclaration,
        serviceClass: KSClassDeclaration,
        outputStream: OutputStream,
        callable: String
    ) {
        LogUtil.log("generateFile-1")

        LogUtil.log("generateFile-2")
        outputStream.writer()
            .use {
                SPICodeGenerator.generator(
                    packageName = packageName,
                    className = callable,
                    serviceClass = serviceClass,
                    singleton = singleton,
                    serviceImplClass = serviceImplClass,
                    priority = priority
                )
                    .writeTo(it)
            }

    }
}

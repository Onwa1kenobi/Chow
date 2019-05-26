package io.julius.chow.data.source

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class DataSourceQualifier(val source: Source)

enum class Source {
    Local, Remote
}
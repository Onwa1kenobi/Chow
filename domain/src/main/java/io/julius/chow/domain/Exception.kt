package io.julius.chow.domain

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureException] class.
 */
sealed class Exception(override val message: String = "An error occurred") : Throwable() {
    object NetworkConnection : Exception("Network error occurred")
    object Error : Exception()

    class LocalDataException(message: String) : FeatureException(message)
    class RemoteDataException(message: String) : FeatureException(message)

    object RemoteDataNotFoundException : FeatureException("Data not found in remote data source")
    object LocalDataNotFoundException : FeatureException("Data not found in local data source")

    /** * Extend this class for feature specific exceptions.*/
    abstract class FeatureException(override var message: String): Exception()
}
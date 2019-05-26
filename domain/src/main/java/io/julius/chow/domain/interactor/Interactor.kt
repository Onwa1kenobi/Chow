package io.julius.chow.domain.interactor

import kotlinx.coroutines.*

/**
 * Interactors are the entry points to the domain layer
 * Each interactor must extend this class
 *
 * @param Type refers to the output or return type for the particular interactor operation
 * @param Params refers to optional additional query parameters (could be a None object)
 */
abstract class Interactor<in Params, out Type> where Type : Any {

    /**
     * Abstract method for each interactor implementation to perform its actual function
     *
     * @param params refers to the respective request values (query params)
     */
    abstract suspend fun run(params: Params) : Type

    fun execute(params: Params, onResult: (Type) -> Unit = {}) {

        val job = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
            run(params)
        }

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            onResult.invoke(job.await())
        }
    }

    class None
}
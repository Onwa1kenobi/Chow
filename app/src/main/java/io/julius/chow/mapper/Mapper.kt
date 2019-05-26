package io.julius.chow.mapper

/**
 * Interface for model mappers. It provides helper methods that facilitate
 * retrieving of models from and to outer layers
 *
 * @param <D> the app presentation model input type (ie the layer internal model type)
 * @param <E> the model return type (the outer layer model type)
 */
interface Mapper<D, E> {

    fun mapToModel(type: D): E

    fun mapFromModel(type: E): D
}
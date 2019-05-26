package io.julius.chow.data.mapper

/**
 * Interface for model mappers. It provides helper methods that facilitate
 * retrieving of models from and to outer layers
 *
 * @param <D> the cached or remote model input type (ie the layer internal model type)
 * @param <E> the model return type (the outer layer model type)
 */
interface Mapper<D, E> {

    fun mapToEntity(type: E): D

    fun mapFromEntity(type: D): E
}
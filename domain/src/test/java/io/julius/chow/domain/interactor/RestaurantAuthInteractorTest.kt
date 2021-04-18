package io.julius.chow.domain.interactor

import Util
import io.julius.chow.domain.Exception.NotImplementedException
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.auth.RestaurantAuthInteractor
import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.domain.repository.RestaurantRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(
    MockitoJUnitRunner::class
)
class RestaurantAuthInteractorTest {

    @get:Rule
    val testCoroutineRule = Util.TestCoroutineRule()

    @Mock
    private lateinit var mockRestaurantRepository: RestaurantRepository

    private lateinit var restaurantAuthInteractor: RestaurantAuthInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        restaurantAuthInteractor = RestaurantAuthInteractor(mockRestaurantRepository)
    }

    @Test
    fun test_authenticateRestaurant(): Unit = testCoroutineRule.runBlockingTest {
        val restaurantModel = RestaurantModel(
            "101", "Cold Cafe", "", "", "Blah blah",
            "", "", "", 0.0, 0.0, true
        )

        `when`(mockRestaurantRepository.authenticateRestaurant())
            .thenReturn(Result.Success(restaurantModel))

        val result = restaurantAuthInteractor.run(RestaurantAuthInteractor.Params())
        assert(result is Result.Success)

        verify(mockRestaurantRepository).authenticateRestaurant()
        verify(mockRestaurantRepository).saveRestaurantLocally(restaurantModel)
    }

    @Test
    fun test_authenticateIncompleteRestaurant(): Unit = testCoroutineRule.runBlockingTest {
        val restaurantModel = RestaurantModel(
            "101", "Cold Cafe", "", "", "Blah blah",
            "", "", "", 0.0, 0.0, false
        )

        `when`(mockRestaurantRepository.authenticateRestaurant())
            .thenReturn(Result.Success(restaurantModel))

        val result = restaurantAuthInteractor.run(RestaurantAuthInteractor.Params())
        assert(result is Result.Success)

        verify(mockRestaurantRepository).authenticateRestaurant()
        verify(mockRestaurantRepository, times(0)).saveRestaurantLocally(restaurantModel)
    }

    @Test
    fun test_authenticateRestaurant_failure(): Unit = testCoroutineRule.runBlockingTest {
        `when`(mockRestaurantRepository.authenticateRestaurant())
            .thenReturn(Result.Failure(NotImplementedException))

        val result = restaurantAuthInteractor.run(RestaurantAuthInteractor.Params())
        assert(result is Result.Failure)
    }

    @Test
    fun test_updateRestaurant_failure(): Unit = testCoroutineRule.runBlockingTest {
        val restaurantModel = RestaurantModel(
            "101", "Cold Cafe", "", "", "Blah blah",
            "", "", "", 0.0, 0.0, false
        )

        `when`(mockRestaurantRepository.saveRestaurant(Util.MockitoHelper.anyObject()))
            .thenReturn(Result.Failure(NotImplementedException))

        val result = restaurantAuthInteractor.run(RestaurantAuthInteractor.Params(
            RestaurantAuthInteractor.Params.ParamType.UPDATE_RESTAURANT_INFO,
            restaurantModel
        ))
        assert(result is Result.Failure)
    }

    @Test
    fun test_updateRestaurant_success(): Unit = testCoroutineRule.runBlockingTest {
        val restaurantModel = RestaurantModel(
            "101", "Cold Cafe", "", "", "Blah blah",
            "", "", "", 0.0, 0.0, false
        )

        `when`(mockRestaurantRepository.saveRestaurant(Util.MockitoHelper.anyObject()))
            .thenReturn(Result.Success(true))

        val result = restaurantAuthInteractor.run(RestaurantAuthInteractor.Params(
            RestaurantAuthInteractor.Params.ParamType.UPDATE_RESTAURANT_INFO,
            restaurantModel
        ))
        assert(result is Result.Success)
        assert((result as Result.Success).data == true)
    }
}
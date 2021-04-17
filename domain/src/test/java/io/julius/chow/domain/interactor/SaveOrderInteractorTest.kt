package io.julius.chow.domain.interactor

import Util
import io.julius.chow.domain.interactor.food.SaveOrderInteractor
import io.julius.chow.domain.model.OrderModel
import io.julius.chow.domain.model.OrderState
import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.domain.repository.ChowRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(
    MockitoJUnitRunner::class
)
class SaveOrderInteractorTest {
    @get:Rule
    val testCoroutineRule = Util.TestCoroutineRule()

    @Mock
    private lateinit var mockChowRepository: ChowRepository

    private lateinit var saveOrderInteractor: SaveOrderInteractor

    private val restaurantModel = RestaurantModel(
        "101", "Cold Cafe", "", "", "Blah blah",
        "", "", "", 0.0, 0.0, true
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        saveOrderInteractor = SaveOrderInteractor(mockChowRepository)
    }

    @Test
    fun test_orderSave_failure(): Unit = testCoroutineRule.runBlockingTest {
        val orderModel = OrderModel(
            "101", "Cold Cafe", "", "", "Blah blah",
            0.0, 0.0, 0, OrderState.ACTIVE, false
        )

        Mockito.`when`(mockChowRepository.getOrder(anyString()))
            .thenReturn(null)
        Mockito.`when`(mockChowRepository.saveOrder(Util.MockitoHelper.anyObject()))
            .thenReturn(false)

        var message = ""

        saveOrderInteractor.execute(orderModel) {
            message = it
        }

        assert(message == "Failed to add order, try again")
    }

    @Test
    fun test_orderSave_success(): Unit = testCoroutineRule.runBlockingTest {
        val orderModel = OrderModel(
            "101", "Cold Cafe", "", "", "Blah blah",
            0.0, 0.0, 0, OrderState.ACTIVE, false
        )

        Mockito.`when`(mockChowRepository.getOrder(anyString()))
            .thenReturn(null)
        Mockito.`when`(mockChowRepository.saveOrder(Util.MockitoHelper.anyObject()))
            .thenReturn(true)

        var message = ""

        saveOrderInteractor.execute(orderModel) {
            message = it
        }

        assert(message == "Order was added successfully")
    }

    @Test
    fun test_orderUpdate_failure(): Unit = testCoroutineRule.runBlockingTest {
        val orderModel = OrderModel(
            "101", "Cold Cafe", "", "", "Blah blah",
            0.0, 0.0, 0, OrderState.ACTIVE, false
        )

        Mockito.`when`(mockChowRepository.getOrder(anyString()))
            .thenReturn(orderModel)
        Mockito.`when`(mockChowRepository.saveOrder(Util.MockitoHelper.anyObject()))
            .thenReturn(false)

        var message = ""

        saveOrderInteractor.execute(orderModel) {
            message = it
        }

        assert(message == "Failed to update order, try again")
    }

    @Test
    fun test_orderUpdate_success(): Unit = testCoroutineRule.runBlockingTest {
        val orderModel = OrderModel(
            "101", "Cold Cafe", "", "", "Blah blah",
            0.0, 0.0, 0, OrderState.ACTIVE, false
        )

        Mockito.`when`(mockChowRepository.getOrder(anyString()))
            .thenReturn(orderModel)
        Mockito.`when`(mockChowRepository.saveOrder(Util.MockitoHelper.anyObject()))
            .thenReturn(true)

        var message = ""

        saveOrderInteractor.execute(orderModel) {
            message = it
        }

        assert(message == "Order was updated successfully")
    }
}
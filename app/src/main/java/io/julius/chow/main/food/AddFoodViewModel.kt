package io.julius.chow.main.food

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.food.SaveFoodInteractor
import io.julius.chow.mapper.FoodMapper
import io.julius.chow.model.Food
import io.julius.chow.util.Event
import javax.inject.Inject

class AddFoodViewModel @Inject constructor(private val saveFoodInteractor: SaveFoodInteractor) : ViewModel() {

    lateinit var food: Food

    // LiveData object for view state interaction
    val foodViewContract: MutableLiveData<Event<FoodViewContract>> = MutableLiveData()

    fun saveFood(
        name: String,
        description: String,
        price: Double,
        category: String,
        imageUri: String
    ) {
        // Display progress bar
        foodViewContract.value = Event(FoodViewContract.ProgressDisplay(true))

        food.apply {
            this.title = name
            this.description = description
            this.price = price
            this.category = category
            this.imageUrl = imageUri
        }

        saveFoodInteractor.execute(FoodMapper.mapToModel(food)) { result ->
            // Hide progress bar
            foodViewContract.value = Event(FoodViewContract.ProgressDisplay(false))
            when (result) {
                is Result.Success -> foodViewContract.postValue(Event(FoodViewContract.SaveSuccess))
                is Result.Failure -> foodViewContract.postValue(
                    Event(FoodViewContract.MessageDisplay(result.exception.message))
                )
            }
        }
    }
}
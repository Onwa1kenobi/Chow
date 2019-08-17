package io.julius.chow.data.source.remote

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.julius.chow.data.mapper.RestaurantEntityMapper
import io.julius.chow.data.mapper.UserEntityMapper
import io.julius.chow.data.model.FoodEntity
import io.julius.chow.data.model.PlacedOrderEntity
import io.julius.chow.data.model.RestaurantEntity
import io.julius.chow.data.model.UserEntity
import io.julius.chow.data.source.DataSource
import io.julius.chow.domain.Exception
import io.julius.chow.domain.Result
import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.domain.model.UserModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Remote Implementation of the [DataSource] interface from the Data layer as it is the Data layer's responsibility
 * for defining the operations in which all Data Source implementation layers can carry out, to provide a means
 * of communicating with the Data Repository.
 */
class RemoteDataSource @Inject constructor() : DataSource {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storageRef: FirebaseStorage = FirebaseStorage.getInstance()

    override suspend fun authenticateUser(): Result<UserModel> {
        // Check if the currently logged user is a new user
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        try {
            val document = db.collection("Users").document(firebaseUser!!.uid).get().await()
            return if (document.exists() && document.toObject(UserEntity::class.java)!!.profileComplete) {
                // The user exists...
                Result.Success(UserEntityMapper.mapFromEntity(document.toObject(UserEntity::class.java)!!))
            } else {
                //The user doesn't exist...
                val currentUser = UserEntity(
                    id = firebaseUser.uid,
                    name = "",
                    address = "",
                    phoneNumber = firebaseUser.phoneNumber!!
                )

                Result.Success(UserEntityMapper.mapFromEntity(currentUser))
            }

        } catch (e: FirebaseFirestoreException) {
            return Result.Failure(Exception.RemoteDataException(e.localizedMessage))
        }
    }

    override suspend fun saveUser(userEntity: UserEntity): Result<Boolean> {
        return try {
            db.collection("Users").document(userEntity.id).set(userEntity, SetOptions.merge()).await()
            // User object saved successfully
            Result.Success(true)
        } catch (e: FirebaseFirestoreException) {
            Result.Failure(Exception.RemoteDataException(e.localizedMessage))
        }
    }

    override suspend fun authenticateRestaurant(): Result<RestaurantModel> {
        // Check if the currently logged user is a new user
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        try {
            val document = db.collection("Restaurants").document(firebaseUser!!.uid).get().await()
            return if (document.exists() && document.toObject(RestaurantEntity::class.java)!!.profileComplete) {
                // The user exists...
                Result.Success(RestaurantEntityMapper.mapFromEntity(document.toObject(RestaurantEntity::class.java)!!))
            } else {
                //The user doesn't exist...
                val currentRestaurant = RestaurantEntity(
                    id = firebaseUser.uid,
                    name = "",
                    emailAddress = "",
                    address = "",
                    imageUrl = "",
                    phoneNumber = firebaseUser.phoneNumber!!,
                    description = "",
                    location = "",
                    locationLongitude = 0.0,
                    locationLatitude = 0.0
                )

                Result.Success(RestaurantEntityMapper.mapFromEntity(currentRestaurant))
            }

        } catch (e: FirebaseFirestoreException) {
            return Result.Failure(Exception.RemoteDataException(e.localizedMessage))
        }
    }

    override suspend fun saveRestaurant(restaurantEntity: RestaurantEntity): Result<Boolean> {
        return try {
            val restaurantImagesRef = storageRef.reference
                .child("restaurantImages/${Uri.parse(restaurantEntity.imageUrl).lastPathSegment}")
            val uploadTask = restaurantImagesRef.putFile(Uri.parse(restaurantEntity.imageUrl))

            val imageUrl: Uri? = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        return@Continuation null
                    }
                }
                return@Continuation restaurantImagesRef.downloadUrl
            }).await()
//        }).addOnCompleteListener { task ->
//            return if (task.isSuccessful) {
//                val downloadUri = task.result
//            } else {
//                // Handle failures
//                // ...
//            }
//        }.await()

            restaurantEntity.apply { this.imageUrl = imageUrl.toString() }

            db.collection("Restaurants").document(restaurantEntity.id).set(restaurantEntity, SetOptions.merge()).await()
            // Restaurant object saved successfully
            Result.Success(true)
        } catch (e: FirebaseException) {
            Result.Failure(Exception.RemoteDataException(e.localizedMessage))
        }
    }

    override suspend fun fetchRestaurants(): Flowable<Result<List<RestaurantEntity>>> =
        Flowable.create<Result<List<RestaurantEntity>>>({
            db.collection("Restaurants").get()
                .addOnSuccessListener { result ->
                    // Initialize list of restaurants to return
                    val restaurants = mutableListOf<RestaurantEntity>()

                    // Add each restaurant to the list
                    for (document in result) {
                        val restaurantEntity = document.toObject(RestaurantEntity::class.java)
                        restaurantEntity.locationLongitude = (document.data["latlng"] as GeoPoint).longitude
                        restaurantEntity.locationLatitude = (document.data["latlng"] as GeoPoint).latitude

                        restaurants.add(restaurantEntity)
                    }

                    // return the list of restaurants
                    it.onNext(Result.Success(restaurants))
//                    it.onComplete()
                }
                .addOnFailureListener { exception ->
                    // Return appropriate error message
                    it.onError(Exception.RemoteDataException(exception.localizedMessage))
                }
        }, BackpressureStrategy.LATEST)
            // Subscribe to perform the remote db request in the background
            // We do not call observeOn() since we also want the observer of this to be notified in the io() thread
            // because we are using this remote function call to make local db changes.
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())

    /*
        return Flowable.generate {
            db.collection("Restaurants").get()
                .addOnSuccessListener { result ->
                    Log.e("CHOW", "Remote Get was a success.")
                    // Initialize list of restaurants to return
                    val restaurants = mutableListOf<RestaurantEntity>()

                    // Add each restaurant to the list
                    for (document in result) {
                        restaurants.add(document.toObject(RestaurantEntity::class.java))
                    }

                    // return the list of restaurants
                    it.onNext(Result.Success(restaurants))
                    it.onComplete()
                }
                .addOnFailureListener { exception ->
                    Log.e("CHOW", "It failed o.")
                    // Return appropriate error message
                    it.onError(Exception.RemoteDataException(exception.localizedMessage))
                }

            it.onNext(Result.Success(listOf()))

//            val restaurantEntity = RestaurantEntity(
//                "qwerty",
//                "Hard Rock Cafe",
//                "https://www.hardrockcafe.com/files/templates/1732/logoHRC01.png",
//                "+2349087542631"
//            )
//
//            it.onNext(Result.Success(listOf(restaurantEntity)))
//            it.onComplete()
        }
*/

    override suspend fun fetchRestaurantMenu(restaurantId: String): Flowable<Result<List<FoodEntity>>> {
        return Flowable.create<Result<List<FoodEntity>>>({
            db.collection("Food")
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnSuccessListener { result ->
                    // Initialize list of food to return
                    val menu = mutableListOf<FoodEntity>()

                    // Add each food to the list
                    for (document in result) {
                        val foodEntity = document.toObject(FoodEntity::class.java)
                        menu.add(foodEntity)
                    }

                    Log.e("CHOW", "${result.size()}")

                    // return the menu
                    it.onNext(Result.Success(menu))
                }
                .addOnFailureListener { exception ->
                    // Return appropriate error message
                    it.onError(Exception.RemoteDataException(exception.localizedMessage))
                }
        }, BackpressureStrategy.LATEST)
            // Subscribe to perform the remote db request in the background
            // We do not call observeOn() since we also want the observer of this to be notified in the io() thread
            // because we are using this remote function call to make local db changes.
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    override suspend fun getMenu(category: String): Flowable<Result<List<FoodEntity>>> {
        return Flowable.create<Result<List<FoodEntity>>>({
            db.collection("Food")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener { result ->
                    // Initialize list of food to return
                    val menu = mutableListOf<FoodEntity>()

                    // Add each food to the list
                    for (document in result) {
                        val foodEntity = document.toObject(FoodEntity::class.java)
                        menu.add(foodEntity)
                    }

                    // return the menu
                    it.onNext(Result.Success(menu))
                }
                .addOnFailureListener { exception ->
                    // Return appropriate error message
                    it.onError(Exception.RemoteDataException(exception.localizedMessage))
                }
        }, BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    override suspend fun placeOrder(placedOrder: PlacedOrderEntity): Result<PlacedOrderEntity> {
        // Update id of placedOrder
        placedOrder.id = db.collection("PlacedOrders").document().id

        return try {
            db.collection("PlacedOrders").document(placedOrder.id).set(placedOrder, SetOptions.merge()).await()

            // Return placed order for local db caching with firebase created id
            Result.Success(placedOrder)
        } catch (e: FirebaseFirestoreException) {
            Result.Failure(Exception.RemoteDataException(e.localizedMessage))
        }
    }
}
package io.julius.chow.data.source.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.julius.chow.data.model.*
import io.julius.chow.data.source.cache.converter.ListConverter
import io.julius.chow.data.source.cache.converter.OrderStateConverter
import io.julius.chow.data.source.cache.converter.PlacedOrderDataConverter
import io.julius.chow.data.source.cache.converter.TimeConverter


@Database(
    entities = [(UserEntity::class), (RestaurantEntity::class), (FoodEntity::class),
        (OrderEntity::class), (PlacedOrderEntity::class)],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    TimeConverter::class,
    PlacedOrderDataConverter::class,
    ListConverter::class,
    OrderStateConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDAO

    companion object {

        private var INSTANCE: AppDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "Present.db"
                    )
//                        .addMigrations(MIGRATION_1_2)
                        .build()
                }
                return INSTANCE!!
            }
        }

//        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE 'User' ADD COLUMN 'currentSessionJsonString' TEXT NOT NULL DEFAULT ''")
//            }
//        }
    }
}
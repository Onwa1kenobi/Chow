package io.julius.chow.data.source.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.julius.chow.data.model.FoodEntity
import io.julius.chow.data.model.OrderEntity
import io.julius.chow.data.model.RestaurantEntity
import io.julius.chow.data.model.UserEntity


@Database(
    entities = [(UserEntity::class), (RestaurantEntity::class), (FoodEntity::class), (OrderEntity::class)],
    version = 1,
    exportSchema = false
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

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'User' ADD COLUMN 'currentSessionJsonString' TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
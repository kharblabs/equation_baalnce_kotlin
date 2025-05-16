package com.kharblabs.equationbalancer2.search_files

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for chemistry data
 */@Database(
    entities = [Compound::class, Reaction::class, ProductIndex::class, ReagentIndex::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reactionDao(): ReactionDao
    abstract fun compoundDao(): CompoundDao
    abstract fun productIndexDao(): ProductIndexDao
    abstract fun reagentIndexDao(): ReagentIndexDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chemistry_updated_new.db"
                )
                    .createFromAsset("databases/chemistry_updated_new.db") // Load from pre-populated database
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
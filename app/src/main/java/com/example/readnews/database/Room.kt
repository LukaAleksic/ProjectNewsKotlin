
package com.example.readnews.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NewsDao {
    @Query("select * from databasenews")
    fun getNews(): LiveData<List<DatabaseNews>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( news: List<DatabaseNews>)
}

@Database(entities = [DatabaseNews::class], version = 7, exportSchema = false)
abstract class NewsDatabase: RoomDatabase() {
    abstract val newsDao: NewsDao
}
private lateinit var INSTANCE: NewsDatabase

fun getDatabase(context: Context): NewsDatabase {
    synchronized(NewsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    NewsDatabase::class.java,
                    "news")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
    return INSTANCE
}

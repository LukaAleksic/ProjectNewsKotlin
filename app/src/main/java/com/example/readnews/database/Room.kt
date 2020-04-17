package com.example.readnews.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.readnews.util.DB_NAME

@Dao
interface NewsDao {
    @Query("select * from databasenews")
    fun getNews(): LiveData<List<DatabaseNews>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(news: List<DatabaseNews>)

    @Query("DELETE FROM databasenews")
    fun deleteAll()
}

@Database(entities = [DatabaseNews::class], version = 9, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract val newsDao: NewsDao
}

private lateinit var instance: NewsDatabase

fun getDatabase(context: Context): NewsDatabase {
    synchronized(NewsDatabase::class.java) {
        if (!::instance.isInitialized) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                NewsDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return instance
}

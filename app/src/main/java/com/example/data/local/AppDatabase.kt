package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Entity(tableName = "offline_schemes")
data class SchemeEntity(
    @PrimaryKey val id: String,
    val schemeName: String,
    val schemeNameHindi: String,
    val category: String,
    val department: String,
    val governmentLevel: String,
    val applicableState: String,
    val shortDescription: String,
    val shortDescriptionHindi: String,
    val benefitSummary: String,
    val benefitSummaryHindi: String,
    val officialLink: String,
    val sourceReference: String,
    val lastVerifiedDate: String,
    val version: Int,
    val isPublished: Boolean
)

@Dao
interface SchemeDao {
    @Query("SELECT * FROM offline_schemes WHERE isPublished = 1")
    suspend fun getAllSchemes(): List<SchemeEntity>

    @Query("SELECT * FROM offline_schemes WHERE category = :category AND isPublished = 1")
    suspend fun getSchemesByCategory(category: String): List<SchemeEntity>

    @Query("SELECT * FROM offline_schemes WHERE (applicableState = 'All India' OR applicableState = :state) AND isPublished = 1")
    suspend fun getSchemesByState(state: String): List<SchemeEntity>

    @Query("""
        SELECT * FROM offline_schemes 
        WHERE (schemeName LIKE '%' || :query || '%' OR schemeNameHindi LIKE '%' || :query || '%' OR shortDescriptionHindi LIKE '%' || :query || '%')
        AND isPublished = 1
    """)
    suspend fun searchSchemes(query: String): List<SchemeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchemes(schemes: List<SchemeEntity>)

    @Query("DELETE FROM offline_schemes")
    suspend fun clearSchemes()
}

@Database(entities = [SchemeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun schemeDao(): SchemeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "yojanamitra_offline.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

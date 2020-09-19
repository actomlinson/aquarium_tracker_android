package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquariumtracker.database.model.Image

@Dao
interface ImageDAO {
    @Query("SELECT * from image_table WHERE aq_id = :aqID")
    fun getImagesForAquarium(aqID: Long): LiveData<List<Image>>

    @Query("SELECT * from image_table ORDER BY aq_id ASC")
    fun getAllImages(): LiveData<List<Image>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(im: Image): Long

    @Query("SELECT * from image_table WHERE uri = :uri")
    fun getImageByURI(uri: String): Image?

    @Query("DELETE FROM image_table WHERE im_id = :imID")
    suspend fun deleteImage(imID: Long)
}

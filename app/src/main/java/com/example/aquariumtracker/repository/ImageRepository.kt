package com.example.aquariumtracker.repository

import android.content.Context
import com.example.aquariumtracker.database.dao.ImageDAO
import com.example.aquariumtracker.database.model.Image

class ImageRepository(private val context: Context, private val imageDAO: ImageDAO) {
    fun getImagesForAquarium(aqID: Long) = imageDAO.getImagesForAquarium(aqID)
    fun getAllImages() = imageDAO.getAllImages()
    suspend fun insert(im: Image) = imageDAO.insert(im)
}
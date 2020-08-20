package com.example.aquariumtracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.database.model.Image
import com.example.aquariumtracker.repository.ImageRepository
import kotlinx.coroutines.withContext

class ImageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ImageRepository

    val allImages: LiveData<List<Image>>

    init {
        val imageDAO = AppDatabase.getDatabase(application, viewModelScope).imageDao()
        repository = ImageRepository(application, imageDAO)
        allImages = repository.getAllImages()
    }

    fun getImagesForAquarium(aqID: Long): LiveData<List<Image>> {
        return repository.getImagesForAquarium(aqID)
    }

    suspend fun insert(im: Image): Long {
        return withContext(viewModelScope.coroutineContext) {
            repository.insert(im)
        }
    }
}
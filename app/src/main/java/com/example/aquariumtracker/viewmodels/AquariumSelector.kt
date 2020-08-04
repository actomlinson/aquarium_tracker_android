package com.example.aquariumtracker.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AquariumSelector() : ViewModel() {

    val selected = MutableLiveData<Long>()

    fun select(i: Long) {
        selected.value = i
    }

}
package com.example.aquariumtracker.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AquariumSelector() : ViewModel() {

    val selected = MutableLiveData<Int>()

    fun select(i: Int) {
        selected.value = i
    }

}
package com.example.anyrecipe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anyrecipe.db.MealDatabase

class MealViewModelFactory(
        private val mealDatabase: MealDatabase
        ): ViewModelProvider.Factory {
            override fun <T: ViewModel> create(modelClass: Class<T>):T{
                return MealViewModel(mealDatabase) as T
            }
}
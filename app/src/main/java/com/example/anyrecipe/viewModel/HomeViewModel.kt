package com.example.anyrecipe.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyrecipe.db.MealDatabase
import com.example.anyrecipe.pojo.Category
import com.example.anyrecipe.pojo.CategoryList
import com.example.anyrecipe.pojo.MealsByCategoryList
import com.example.anyrecipe.pojo.MealsByCategory
import com.example.anyrecipe.pojo.Meal
import com.example.anyrecipe.pojo.MealList
import com.example.anyrecipe.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var randomMealLiveData = MutableLiveData<Meal>()
private var popularItemsLiveData = MutableLiveData<List<MealsByCategory>>()
private var categoriesLiveData = MutableLiveData<List<Category>>()
private var bottomSheetMealLiveData = MutableLiveData<Meal>()
private val searchedMealsLiveData = MutableLiveData<List<Meal>>()

class HomeViewModel(
    private val mealDatabase: MealDatabase
): ViewModel(){
    private var favoriteMealsLiveData = mealDatabase.mealDao().getAllMeals()
    private var saveStateRandomMeal : Meal? = null
    fun getRandomMeal(){
        saveStateRandomMeal?.let {randomMeal ->
            randomMealLiveData.postValue(randomMeal)
            return
        }
        RetrofitInstance.api.getRandomMeal().enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body() != null){
                    val randomMeal: Meal = response.body()!!.meals[0]
                    //Log.d("Test", "meal id ${randomMeal.idMeal} name ${randomMeal.strMeal}")
                    randomMealLiveData.value = randomMeal
                    saveStateRandomMeal = randomMeal
                }else{
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("HomeFragment", t.message.toString())
            }
        })
    }

    fun observRandomMealLivedata():LiveData<Meal>{
        return randomMealLiveData
    }

    fun getPopularItems(){
        RetrofitInstance.api.getPopularItems("Seafood").enqueue(object : Callback<MealsByCategoryList>{
            override fun onResponse(call: Call<MealsByCategoryList>, response: Response<MealsByCategoryList>) {
                if (response.body() != null){
                    popularItemsLiveData.value = response.body()!!.meals
                }
            }

            override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                Log.d("HomeFragment", t.message.toString())
            }

        })
    }
    fun observerPopularItemsLiveData():LiveData<List<MealsByCategory>>{
        return popularItemsLiveData
    }

    fun getCategories(){
        RetrofitInstance.api.getCategories().enqueue(object : Callback<CategoryList>{
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {
                response.body()?.let { categoryList ->
                    categoriesLiveData.postValue(categoryList.categories)
                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Log.d("HomeViewModel", t.message.toString())
            }

        })
    }
    fun insertMeal(meal: Meal){
        viewModelScope.launch {
            mealDatabase.mealDao().upsert(meal)
        }
    }

    fun deleteMeal(meal: Meal){
        viewModelScope.launch {
            mealDatabase.mealDao().delete(meal)
        }
    }
    fun observeCategoriesLiveData(): LiveData<List<Category>>{
        return categoriesLiveData
    }
    fun observeFavoritesMealsLiveData(): LiveData<List<Meal>>{
        return favoriteMealsLiveData
    }
    fun getMealById(id: String){
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val meal = response.body()?.meals?.first()
                meal?.let {meal->
                    bottomSheetMealLiveData.postValue(meal)
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("HomeViewModel", t.message.toString())
            }

        })
    }
    fun observeBottomSheetMeal():LiveData<Meal> = bottomSheetMealLiveData

    fun searchMeals(searchQuery: String) = RetrofitInstance.api.searchMeal(searchQuery).enqueue(
        object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val mealsList = response.body()?.meals
                mealsList?.let {
                    searchedMealsLiveData.postValue(it)
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("HomeViewModel", t.message.toString())
            }

        }
    )
    fun observeSearchedMealsLiveData(): LiveData<List<Meal>> = searchedMealsLiveData

}
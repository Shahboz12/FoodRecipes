package com.example.anyrecipe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager

import com.example.anyrecipe.adapters.CategoryMealsAdapter
import com.example.anyrecipe.databinding.ActivityCategoryMealsBinding
import com.example.anyrecipe.fragments.FavoritesFragment

import com.example.anyrecipe.fragments.HomeFragment
import com.example.anyrecipe.viewModel.CategoryMealsViewModel
import com.example.anyrecipe.viewModel.HomeViewModel


class CategoryMealsActivity :AppCompatActivity() {

    lateinit var binding: ActivityCategoryMealsBinding
    lateinit var categoryMealsViewModel: CategoryMealsViewModel
    lateinit var categoryMealsAdapter: CategoryMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareRecyclerView()

        categoryMealsViewModel = ViewModelProviders.of(this)[CategoryMealsViewModel::class.java]

        categoryMealsViewModel.getMealsByCategory(intent.getStringExtra(HomeFragment.CATEGORY_NAME)!!)

        categoryMealsViewModel.observeMealsLiveData().observe(this, Observer{mealsList ->
            binding.tvCategoryCount.text = mealsList.size.toString()
            categoryMealsAdapter.setMealsList(mealsList)
        })

        onCategoryItemClick()
    }

    private fun prepareRecyclerView() {
        categoryMealsAdapter = CategoryMealsAdapter()
        binding.rvMeals.apply {
            layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL, false)
            adapter = categoryMealsAdapter
        }
    }
    //my changes
    private fun onCategoryItemClick() {
        categoryMealsAdapter.onItemClick = {meal ->
            val intent = Intent(this, MealActivity::class.java)
            intent.putExtra(FavoritesFragment.MEAL_ID, meal.idMeal)
            intent.putExtra(FavoritesFragment.MEAL_NAME, meal.strMeal)
            intent.putExtra(FavoritesFragment.MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }


}
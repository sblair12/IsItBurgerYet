package com.sblair.isitburgeryet

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sblair.isitburgeryet.model.RecipeSearch
import com.sblair.isitburgeryet.viewmodel.RecipeViewModel
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.recipe_list.view.*
import java.util.ArrayList

class RecipeViewActivity : AppCompatActivity() {

    lateinit var recipe: RecipeSearch
    lateinit var ingredients: ArrayList<String>
    private var adapter = IngredientsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        recipe = intent.getSerializableExtra("RECIPE_SEARCH") as RecipeSearch
        ingredients = recipe.ingredients

        recipeTitle.text = recipe.title
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addToList.setOnClickListener {
            var viewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)

            viewModel.addRecipe(-1, recipe.title, recipe.ingredients.joinToString(","), recipe.href, recipe.thumbnail)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    inner class IngredientsAdapter: RecyclerView.Adapter<IngredientsAdapter.RecipeIngredientsViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecipeIngredientsViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.ingredients_list, p0, false)
            return RecipeIngredientsViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: RecipeIngredientsViewHolder, p1: Int) {
            val ingredient = ingredients[p1]
            p0.titleTextView.text = ingredient
        }

        override fun getItemCount(): Int {
            return ingredients.size
        }

        inner class RecipeIngredientsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var titleTextView: TextView = itemView.title
        }
    }
}

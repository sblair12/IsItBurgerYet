package com.sblair.isitburgeryet

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.sblair.isitburgeryet.model.Recipe
import com.sblair.isitburgeryet.model.RecipeSearch
import com.sblair.isitburgeryet.viewmodel.RecipeViewModel
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.recipe_list.view.*
import java.util.ArrayList

class MyRecipeViewActivity : AppCompatActivity() {

    private lateinit var recipe: Recipe
    private lateinit var ingredients: ArrayList<String>
    private var adapter = IngredientsAdapter()
    private lateinit var viewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_recipe_view)

        viewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
        recipe = intent.getSerializableExtra("RECIPE_SEARCH") as Recipe
        ingredients = ArrayList(recipe.ingredients.split("~"))

        recipeTitle.text = recipe.title
        link.text = recipe.href
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addToList.setOnClickListener {
            viewModel.addToShoppingList(recipe)
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

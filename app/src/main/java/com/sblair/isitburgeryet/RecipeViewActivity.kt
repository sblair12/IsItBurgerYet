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
import com.sblair.isitburgeryet.model.RecipeSearch
import com.sblair.isitburgeryet.viewmodel.RecipeViewModel
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.recipe_list.view.*
import java.util.ArrayList

class RecipeViewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var recipe: RecipeSearch
    private lateinit var ingredients: ArrayList<String>
    private var adapter = IngredientsAdapter()
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var viewModel: RecipeViewModel
    private var categoryList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        viewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
        recipe = intent.getSerializableExtra("RECIPE_SEARCH") as RecipeSearch
        ingredients = recipe.ingredients

        recipeTitle.text = recipe.title
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        categoryList.add("No category")
        categoryList.add("New category")
        categoryList.addAll(viewModel.getCategories())

        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryList)
        chooseCategory.adapter = categoryAdapter
        chooseCategory.onItemSelectedListener = this

        addToList.setOnClickListener {
            var viewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)

            viewModel.addRecipe(-1, recipe.title, recipe.ingredients.joinToString(","), recipe.href, recipe.thumbnail, recipe.category)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.getItemAtPosition(position)) {
            "New category" -> {
                val categoryEditText = EditText(this)
                val dialog = AlertDialog.Builder(this)
                    .setTitle("New Category")
                    .setMessage("Create a category for your recipe")
                    .setView(categoryEditText)
                    .setPositiveButton("Add") { _, _ ->
                        if (recipe.category != "") {
                            categoryList.remove(recipe.category)
                        }
                        val category = categoryEditText.text.toString()
                        recipe.category = category
                        categoryList.add(category)
                        parent.setSelection(categoryAdapter.getPosition(category))
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                dialog.show()
            }
            "No category" -> recipe.category = ""
            else -> recipe.category = parent?.getItemAtPosition(position) as String
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //do nothing
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

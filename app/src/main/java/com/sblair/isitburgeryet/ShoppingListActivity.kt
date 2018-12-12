package com.sblair.isitburgeryet

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.sblair.isitburgeryet.model.Ingredient
import com.sblair.isitburgeryet.model.Recipe
import com.sblair.isitburgeryet.viewmodel.RecipeLiveData.recipeList
import com.sblair.isitburgeryet.viewmodel.RecipeViewModel
import kotlinx.android.synthetic.main.activity_shopping_list.*
import kotlinx.android.synthetic.main.ingredient_check.view.*
import kotlinx.android.synthetic.main.recipe_ingredients.view.*
import java.util.ArrayList

class ShoppingListActivity : AppCompatActivity() {

    private var ingredientList = ArrayList<Ingredient>()
    private var recipeNames = ArrayList<String>()
    private var recipeMap = HashMap<String, ArrayList<Ingredient>>()
    private var adapter = IngredientAdapter()
    private lateinit var viewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        nav_view.menu.getItem(2).setChecked(true)
        nav_view.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawer_layout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
            }

            true
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
        val observer = Observer<ArrayList<Ingredient>> {
            recyclerView.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return ingredientList.size
                }

                override fun getNewListSize(): Int {
                    return it!!.size
                }

                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return ingredientList[p0].id == it!![p1].id
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return ingredientList[p0] == it!![p1]
                }
            })
            result.dispatchUpdatesTo(adapter) // update the adapter
            ingredientList = it!!
            recipeNames = ArrayList<String>()
            recipeMap = HashMap<String, ArrayList<Ingredient>>()

            for (ingredient in ingredientList) {
                if (!recipeNames.contains(ingredient.recipeName)) {
                    recipeNames.add(ingredient.recipeName)
                    recipeMap[ingredient.recipeName] = ArrayList<Ingredient>()
                    recipeMap[ingredient.recipeName]!!.add(ingredient)
                }
                else {
                    recipeMap[ingredient.recipeName]!!.add(ingredient)
                }
            }
        }
        viewModel.getShoppingList().observe(this, observer)
    }

    inner class IngredientAdapter: RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): IngredientViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.recipe_ingredients, p0, false)
            return IngredientViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: IngredientViewHolder, p1: Int) {
            val recipe = recipeNames[p1]
            val ingredients = recipeMap[recipe]!!
            p0.titleTextView.text = recipe

            for (ingredient in ingredients) {
                val layout = LayoutInflater.from(this@ShoppingListActivity).inflate(R.layout.ingredient_check, null)
                layout.checked.isChecked = if (ingredient.checked == 1) true else false
                layout.ingredientText.text = ingredient.name
                p0.ingredientLayout.addView(layout)
            }
        }

        override fun getItemCount(): Int {
            return recipeNames.size
        }

        inner class IngredientViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var relativeLayout: RelativeLayout = itemView.relLayout
            var ingredientLayout: LinearLayout = itemView.ingredientsHolder
            var titleTextView: TextView = itemView.title

            init {

            }
        }
    }
}

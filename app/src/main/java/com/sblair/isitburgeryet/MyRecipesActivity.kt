package com.sblair.isitburgeryet

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sblair.isitburgeryet.model.Recipe
import com.sblair.isitburgeryet.model.RecipeSearch
import com.sblair.isitburgeryet.viewmodel.RecipeViewModel
import kotlinx.android.synthetic.main.activity_my_recipes.*
import kotlinx.android.synthetic.main.recipe_list.view.*
import java.util.ArrayList

class MyRecipesActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var recipeList = ArrayList<Recipe>()
    private var adapter = RecipeSearchAdapter()
    private lateinit var viewModel: RecipeViewModel
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private var categoryList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_recipes)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        nav_view.menu.getItem(0).setChecked(true)
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

        val observer = Observer<ArrayList<Recipe>> {
            recyclerView.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return recipeList.size
                }

                override fun getNewListSize(): Int {
                    return it!!.size
                }

                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return recipeList[p0].id == it!![p1].id
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return recipeList[p0] == it!![p1]
                }
            })
            result.dispatchUpdatesTo(adapter) // update the adapter
            recipeList = it!!
        }

        categoryList.addAll(viewModel.getCategories())

        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryList)
        categories.adapter = categoryAdapter
        categories.onItemSelectedListener = this
        viewModel.getRecipes(categories.selectedItem.toString()).observe(this, observer)
    }

    override fun onResume() {
        super.onResume()
        nav_view.menu.getItem(1).setChecked(true)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.getRecipes(parent?.getItemAtPosition(position) as String)
    }

    // inner here gives this class to variables in the MainActivity class
    inner class RecipeSearchAdapter: RecyclerView.Adapter<RecipeSearchAdapter.RecipeSearchViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecipeSearchViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.recipe_list, p0, false)
            return RecipeSearchViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: RecipeSearchViewHolder, p1: Int) {
            val recipe = recipeList[p1]
            p0.titleTextView.text = recipe.title
            ImageUrlTask(p0.thumbnailView).execute(recipe.thumbnail)
            p0.itemView.setOnClickListener {
                val intent = Intent(this@MyRecipesActivity, MyRecipeViewActivity::class.java)
                intent.putExtra("RECIPE_SEARCH", recipe)
                startActivityForResult(intent, 1)
            }
        }

        override fun getItemCount(): Int {
            return recipeList.size
        }

        inner class RecipeSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var titleTextView: TextView = itemView.title
            var thumbnailView: ImageView = itemView.image

            init {

            }
        }
    }

    private inner class ImageUrlTask(internal var imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            val url = urls[0]
            var icon: Bitmap? = null
            try {
                val stream = java.net.URL(url).openStream()
                icon = BitmapFactory.decodeStream(stream)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }

            return icon
        }

        override fun onPostExecute(result: Bitmap) {
            imageView.setImageBitmap(result)
        }
    }
}

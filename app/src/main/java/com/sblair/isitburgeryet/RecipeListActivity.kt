package com.sblair.isitburgeryet

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sblair.isitburgeryet.model.Recipe
import com.sblair.isitburgeryet.viewmodel.RecipeLiveData.recipeList
import kotlinx.android.synthetic.main.recipe_list.view.*
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.sblair.isitburgeryet.model.RecipeSearch
import kotlinx.android.synthetic.main.activity_recipe_list.*


class RecipeListActivity : AppCompatActivity() {

    lateinit var recipeResultList: ArrayList<RecipeSearch>
    private var adapter = RecipeSearchAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        recipeResultList = intent.getSerializableExtra("RESULT_ARRAY") as ArrayList<RecipeSearch>

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // inner here gives this class to variables in the MainActivity class
    inner class RecipeSearchAdapter: RecyclerView.Adapter<RecipeSearchAdapter.RecipeSearchViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecipeSearchViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.recipe_list, p0, false)
            return RecipeSearchViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: RecipeSearchViewHolder, p1: Int) {
            val recipe = recipeResultList[p1]
            p0.titleTextView.text = recipe.title
            ImageUrlTask(p0.thumbnailView).execute(recipe.thumbnail)
        }

        override fun getItemCount(): Int {
            return recipeResultList.size
        }

        inner class RecipeSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var titleTextView: TextView = itemView.title
            var thumbnailView: ImageView = itemView.image

            init {
                itemView.setOnClickListener {

                }
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

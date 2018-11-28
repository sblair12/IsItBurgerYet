package com.sblair.isitburgeryet

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.sblair.isitburgeryet.model.Recipe
import com.sblair.isitburgeryet.model.RecipeSearch
import com.sblair.isitburgeryet.query.QueryUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var recipeResultList: ArrayList<RecipeSearch>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBtn.setOnClickListener {
            object : Thread() {
                override fun run() {
                    var create = ingredients.text.toString()
                    val resultList = QueryUtils.fetchRecipeData(create)

                    if (resultList != null) {
                        this@MainActivity.recipeResultList = resultList
                        val intent = Intent(this@MainActivity, RecipeListActivity::class.java)
                        intent.putExtra("RESULT_ARRAY", recipeResultList)
                        startActivity(intent)
                    }
                }
            }.start()
        }
    }
}

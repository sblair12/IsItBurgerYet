package com.sblair.isitburgeryet

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.view.MenuItem
import android.widget.TextView
import com.sblair.isitburgeryet.R.id.nav_recipe
import com.sblair.isitburgeryet.model.Recipe
import com.sblair.isitburgeryet.model.RecipeSearch
import com.sblair.isitburgeryet.query.QueryUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var recipeResultList: ArrayList<RecipeSearch>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        nav_view.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawer_layout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_recipe -> startActivity(Intent(this, MyRecipesActivity::class.java))
            }

            true
        }

        searchBtn.setOnClickListener {
            object : Thread() {
                override fun run() {
                    var create = search.text.toString()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

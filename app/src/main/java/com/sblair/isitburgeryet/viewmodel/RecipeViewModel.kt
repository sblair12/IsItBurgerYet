package com.sblair.isitburgeryet.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.sblair.isitburgeryet.R.id.*
import com.sblair.isitburgeryet.model.Recipe
import com.sblair.isitburgeryet.db.DbSettings
import com.sblair.isitburgeryet.db.RecipeDatabaseHelper
import com.sblair.isitburgeryet.viewmodel.RecipeLiveData.recipeList

// Implements all necessary functions for an Item entity in the database

class RecipeViewModel(application: Application): AndroidViewModel(application) {
    private var _recipeDBHelper: RecipeDatabaseHelper = RecipeDatabaseHelper(application)
    private var _recipeList: MutableLiveData<ArrayList<Recipe>> = RecipeLiveData.recipeList // Here's where the magic happens

    fun getRecipes(): MutableLiveData<ArrayList<Recipe>> {
        loadRecipes()
        return _recipeList
    }

    private fun loadRecipes() {
        val newRecipes: ArrayList<Recipe> = ArrayList()
        val database: SQLiteDatabase = this._recipeDBHelper.readableDatabase

        // Check Shared Preferences
        val cursor: Cursor

        cursor = database.query(
            DbSettings.DBEntry.TABLE,
            arrayOf(
                DbSettings.DBEntry.ID,
                DbSettings.DBEntry.COL_TITLE,
                DbSettings.DBEntry.COL_INGREDIENTS,
                DbSettings.DBEntry.COL_HREF,
                DbSettings.DBEntry.COL_IMAGE
            ),
            null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val cursorId = cursor.getColumnIndex(DbSettings.DBEntry.ID)
            val cursorTitle = cursor.getColumnIndex(DbSettings.DBEntry.COL_TITLE)
            val cursorIngredients = cursor.getColumnIndex(DbSettings.DBEntry.COL_INGREDIENTS)
            val cursorHref = cursor.getColumnIndex(DbSettings.DBEntry.COL_HREF)
            val cursorImage = cursor.getColumnIndex(DbSettings.DBEntry.COL_IMAGE)
            newRecipes.add(
                Recipe(
                    cursor.getLong(cursorId),
                    cursor.getString(cursorTitle),
                    cursor.getString(cursorIngredients),
                    cursor.getString(cursorHref),
                    cursor.getString(cursorImage)
                )
            )
        }

        cursor.close()
        database.close()
        this._recipeList.value = newRecipes
    }

    fun addRecipe(oldId: Long, title: String, ingredients: String, href: String, image: String) {
        val database: SQLiteDatabase = _recipeDBHelper.writableDatabase
        val values = ContentValues()
        values.put(DbSettings.DBEntry.COL_TITLE, title)
        values.put(DbSettings.DBEntry.COL_INGREDIENTS, ingredients)
        values.put(DbSettings.DBEntry.COL_HREF, href)
        values.put(DbSettings.DBEntry.COL_IMAGE, image)

        var newId: Long
        var recipeList: ArrayList<Recipe>? = this._recipeList.value
        if (recipeList == null) {
            recipeList = ArrayList()
        }

        if (oldId == -1L) {
            newId = database.insertWithOnConflict( // general method of inserting into the database
                DbSettings.DBEntry.TABLE,
                null,             // allows insertion of null values into the db in the event that values doesn't contain something
                values,
                SQLiteDatabase.CONFLICT_IGNORE // an algorithm for resolving value conflicts
            )
            recipeList.add(
                Recipe(
                    newId,
                    title,
                    ingredients,
                    href,
                    image
                )
            )
        }
        else {
            newId = oldId
            database.updateWithOnConflict(
                DbSettings.DBEntry.TABLE,
                values,
                "_id=$oldId",
                null,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            var index = -1
            for (i in 0 until recipeList.size) {
                if (recipeList[i].id == newId) {
                    index = i
                }
            }
            recipeList[index] =
                    Recipe(
                        newId,
                        title,
                        ingredients,
                        href,
                        image
                    )
        }
        database.close()

        this._recipeList.value = recipeList
    }

    fun removeRecipe(id: Long) {
        val database: SQLiteDatabase = _recipeDBHelper.writableDatabase
        database.delete(
            DbSettings.DBEntry.TABLE,
            DbSettings.DBEntry.ID + " = ?",
            arrayOf(id.toString())
        )
        database.close()

        var index = 0
        val recipes: ArrayList<Recipe>? = this._recipeList.value
        if (recipes != null) {
            for (i in 0 until recipes.size) {
                if (recipes[i].id == id) {
                    index = i
                }
            }
            recipes.removeAt(index)
            this._recipeList.value = recipes
        }
    }

/*    fun checkRecipe(id: Long, checked: Int) {
        val database: SQLiteDatabase = _recipeDBHelper.writableDatabase
        val values = ContentValues()
        values.put(DbSettings.DBEntry.COL_DONE, checked)

        var todoList: ArrayList<Todo>? = this._todoList.value
        if (todoList == null) {
            todoList = ArrayList()
        }

        database.updateWithOnConflict(
            DbSettings.DBEntry.TABLE,
            values,
            "_id=$id",
            null,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        var index = -1
        for (i in 0 until todoList.size) {
            if (todoList[i].id == id) {
                index = i
            }
        }
        todoList[index].done = checked

        database.close()
        this._todoList.value = todoList
    }*/

    fun getRecipe(id: Long) : Recipe {
        var result: Recipe
        val database: SQLiteDatabase = this._recipeDBHelper.readableDatabase
        val cursor: Cursor = database.query(
            DbSettings.DBEntry.TABLE,
            arrayOf(
                DbSettings.DBEntry.ID,
                DbSettings.DBEntry.COL_TITLE,
                DbSettings.DBEntry.COL_INGREDIENTS,
                DbSettings.DBEntry.COL_HREF,
                DbSettings.DBEntry.COL_IMAGE
            ),
            "_id=$id", null, null, null, null
        )

        cursor.moveToNext()
        val cursorId = cursor.getColumnIndex(DbSettings.DBEntry.ID)
        val cursorTitle = cursor.getColumnIndex(DbSettings.DBEntry.COL_TITLE)
        val cursorIngredients = cursor.getColumnIndex(DbSettings.DBEntry.COL_INGREDIENTS)
        val cursorHref = cursor.getColumnIndex(DbSettings.DBEntry.COL_HREF)
        val cursorImage = cursor.getColumnIndex(DbSettings.DBEntry.COL_IMAGE)
        result = Recipe(
            cursor.getLong(cursorId),
            cursor.getString(cursorTitle),
            cursor.getString(cursorIngredients),
            cursor.getString(cursorHref),
            cursor.getString(cursorImage)
        )

        cursor.close()
        database.close()
        return result
    }

/*    fun getCategories() : ArrayList<String> {
        val categories: ArrayList<String> = ArrayList()
        val database: SQLiteDatabase = this._todoDBHelper.readableDatabase

        val cursor: Cursor

        cursor = database.query(
            DbSettings.DBEntry.TABLE,
            arrayOf(
                DbSettings.DBEntry.COL_CATEGORY
            ),
            null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val cursorCategory = cursor.getColumnIndex(DbSettings.DBEntry.COL_CATEGORY)
            val category = cursor.getString(cursorCategory)
            if (!categories.contains(category) && category != "") {
                categories.add(category)
            }
        }

        cursor.close()
        database.close()
        return categories
    }*/
}

object RecipeLiveData {
    var recipeList: MutableLiveData<ArrayList<Recipe>> = MutableLiveData()
}
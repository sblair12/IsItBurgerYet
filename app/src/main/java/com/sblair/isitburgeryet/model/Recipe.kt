package com.sblair.isitburgeryet.model

import java.io.Serializable

class Recipe(var id: Long, var title: String, var ingredients: String, var href: String, var thumbnail: String, var category: String) : Serializable

class RecipeSearch(var title: String, var ingredients: ArrayList<String>, var href: String, var thumbnail: String, var category: String) : Serializable

class Shopping()
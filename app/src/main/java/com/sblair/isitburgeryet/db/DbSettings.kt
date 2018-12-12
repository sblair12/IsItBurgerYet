package com.sblair.isitburgeryet.db

import android.provider.BaseColumns

// This essentially holds static variables that we will need to reference when working with
// the database

class DbSettings {
    companion object {
        const val DB_NAME = "recipe.db"
        const val DB_VERSION = 1
    }

    class DBEntry: BaseColumns {
        companion object {
            const val ID = BaseColumns._ID

            const val TABLE = "recipe"
            const val COL_TITLE = "title"
            const val COL_INGREDIENTS = "ingredients"
            const val COL_HREF = "href"
            const val COL_IMAGE = "image"
            const val COL_CATEGORY = "category"

            const val TABLE_SHOPPING = "shopping"
            const val COL_NAME = "name"
            const val COL_RECIPE_NAME = "recipe"
            const val COL_CHECKED = "checked"
        }
    }
}
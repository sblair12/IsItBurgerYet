package com.sblair.isitburgeryet.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// This essentially creates and upgrades

class RecipeDatabaseHelper(context: Context): SQLiteOpenHelper(context, DbSettings.DB_NAME, null, DbSettings.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        var createTableQuery = "CREATE TABLE " + DbSettings.DBEntry.TABLE + " ( " +
                DbSettings.DBEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSettings.DBEntry.COL_TITLE + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_INGREDIENTS + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_HREF + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_IMAGE + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_CATEGORY + " TEXT NOT NULL); "
        db?.execSQL(createTableQuery)

        createTableQuery = "CREATE TABLE " + DbSettings.DBEntry.TABLE_SHOPPING + " ( " +
                DbSettings.DBEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSettings.DBEntry.COL_NAME + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_RECIPE_NAME + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_CHECKED + " INTEGER NOT NULL); "
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + DbSettings.DBEntry.TABLE)
        onCreate(db)
    }
}
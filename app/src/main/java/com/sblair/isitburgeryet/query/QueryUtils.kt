package com.sblair.isitburgeryet.query

import android.text.TextUtils
import android.util.Log
import com.sblair.isitburgeryet.model.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class QueryUtils {

    companion object {
        private val LogTag = this::class.java.simpleName
        private const val BaseUrl = "https://api.edamam.com/search?q=*INGRED*&app_id=35b2866e&app_key=a0573d3e9e5eaa3bb79b75ecf26beb45&from=0&to=10"

        fun fetchRecipeData(requestUrl: String): ArrayList<RecipeSearch>? {
            val url: URL? = createUrl(this.BaseUrl.replace("*INGRED*", requestUrl))

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractDataFromJson(jsonResponse)
        }

        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            }
            catch (e: MalformedURLException) {
                Log.e(this.LogTag, "Problem building the URL.", e)
            }

            return url
        }

        private fun makeHttpRequest(url: URL?): String {
            var jsonResponse = ""

            if (url == null) {
                return jsonResponse
            }

            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 10000 // 10 seconds
                urlConnection.connectTimeout = 15000 // 15 seconds
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                }
                else {
                    Log.e(this.LogTag, "Error response code: ${urlConnection.responseCode}")
                }
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem retrieving the recipe data results.", e)
            }
            finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }

            return jsonResponse
        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }

            return output.toString()
        }

        private fun extractDataFromJson(recipeJson: String?): ArrayList<RecipeSearch>? {
            if (TextUtils.isEmpty(recipeJson)) {
                return null
            }

            val recipeResultList = ArrayList<RecipeSearch>()
            try {
                val baseJsonResponse = JSONObject(recipeJson)

                val recipeArray = baseJsonResponse.getJSONArray("hits")
                for (i in 0 until recipeArray.length()) {
                    val recipeResult = recipeArray.getJSONObject(i)
                    val recipe = recipeResult.getJSONObject("recipe")
                    val title = recipe.getString("label")
                    val href = recipe.getString("url")
                    val ingredients = ArrayList<String>()

                    val ingredientsArray = recipe.getJSONArray("ingredientLines")
                    for (i in 0 until ingredientsArray.length()) {
                        val ingredient = ingredientsArray.getString(i)
                        ingredients.add(ingredient)
                    }
                    val thumbnail = recipe.getString("image")

                    val recipeObject = RecipeSearch(title, ingredients, href, thumbnail)
                    recipeResultList.add(recipeObject)
                }
            }
            catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the air quality JSON results", e)
            }

            return recipeResultList
        }

        private fun stringToDate(dateString: String, format: String): Date {
            val dateFormat = SimpleDateFormat(format, Locale.US)
            return dateFormat.parse(dateString, ParsePosition(0))
        }
    }


}
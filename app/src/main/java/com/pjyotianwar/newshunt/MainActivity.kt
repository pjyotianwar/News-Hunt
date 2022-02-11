package com.pjyotianwar.newshunt

import android.content.Context
import android.net.Uri
import android.nfc.tech.TagTechnology
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import android.view.inputmethod.InputMethodManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity(), NewsItemClicked {
    lateinit var adapter:NewsListAdapter
    lateinit var search: ImageButton
    lateinit var searchText: EditText
    lateinit var category: ChipGroup
    lateinit var progressBar: ProgressBar
    lateinit var refresh: SwipeRefreshLayout
    lateinit var business: Chip
    lateinit var entertainment: Chip
    lateinit var general: Chip
    lateinit var science: Chip
    lateinit var sports: Chip
    lateinit var health: Chip
    lateinit var technology: Chip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search = findViewById(R.id.search)
        category = findViewById(R.id.category)
        searchText = findViewById(R.id.searchText)
        progressBar = findViewById(R.id.progressBar)
        business = findViewById(R.id.business)
        entertainment = findViewById(R.id.entertainment)
        general = findViewById(R.id.general)
        science = findViewById(R.id.science)
        sports = findViewById(R.id.sports)
        health = findViewById(R.id.health)
        technology = findViewById(R.id.technology)
        refresh = findViewById(R.id.refresh)


        val newsRecyclerView = findViewById<RecyclerView>(R.id.newsItem)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)

        progressBar.visibility = View.VISIBLE
        fetchdata()
        progressBar.visibility = View.GONE

        search.setOnClickListener {
            if(searchText.text.toString().isNotEmpty())
            {
                progressBar.visibility = View.VISIBLE

                val srch=searchText.text.toString()
                val u = "https://newsapi.org/v2/everything?q=$srch&language=en&apiKey=f0095bf95c3b467388fc814e4221d118"
                fetchdata(u)
                progressBar.visibility = View.GONE
                Log.v("Main", "$u")
            }
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            category.clearCheck()
        }
        category.setOnCheckedChangeListener { group, checkedId ->
            if(category.checkedChipId!=-1){
                searchText.setText("")
                progressBar.visibility = View.VISIBLE
                var cat=""
                if(business.isChecked){
                    cat="&category=business"
                }
                else if(entertainment.isChecked){
                    cat ="&category=entertainment"
                }
                else if(general.isChecked){
                    cat ="&category=general"
                }
                else if(science.isChecked){
                    cat ="&category=science"
                }
                else if(sports.isChecked){
                    cat ="&category=sports"
                }
                else if(health.isChecked){
                    cat ="&category=health"
                }
                else if(technology.isChecked){
                    cat ="&category=technology"
                }

                val u = "https://newsapi.org/v2/top-headlines?language=en$cat&apiKey=f0095bf95c3b467388fc814e4221d118"
                fetchdata(u)
                progressBar.visibility = View.GONE
                Log.v("Main", "$u")
            }
        }
        refresh.setOnRefreshListener {
            searchText.setText("")
            category.clearCheck()
            fetchdata()
            refresh.isRefreshing = false
        }

        adapter = NewsListAdapter(this)
        newsRecyclerView.adapter = adapter
    }

    override fun onItemClicked(item: NewsItem) {
        val builder =  CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.Url))
    }

    fun fetchdata(url: String = "https://newsapi.org/v2/top-headlines?country=in&language=en&apiKey=f0095bf95c3b467388fc814e4221d118") {
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<NewsItem>()
                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = NewsItem(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("description"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }

                adapter.updateNews(newsArray)
            },
            Response.ErrorListener {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }

            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
}
package com.arjun.learningone

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import kotlin.properties.Delegates


class DataMap {
    var title: String = ""
    var releaseDate: String = ""
    var link: String = ""
    var artist: String = ""

    override fun toString(): String {
        return """
            title: $title
            releasedDate : $releaseDate
            link : $link
            artist: $artist
        """.trimIndent()
    }
}


class MainActivity : AppCompatActivity() {
    private val refreshLayout: SwipeRefreshLayout by lazy { findViewById(R.id.swipeLayout) }
    private var limit = 10
    private var cachedUrl = "unCached"
    private var urlToDownload = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            limit = savedInstanceState.getInt("limit")
            urlToDownload = savedInstanceState.getString("link").toString()
        }

        refreshLayout.setOnRefreshListener {
            cachedUrl = "unCached"
            CoroutineScope(IO).launch {
                makeDownload(urlToDownload.format(limit))
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
           internetDownload(urlToDownload.format(10))

        }

    }

    private suspend fun internetDownload(link:String){
        val data = downloadXML(link)
        withContext(Dispatchers.Main){
            dataTOUI(data)
        }
    }

    private fun dataTOUI(data:String){
        val parsedData = ParseXML()
        parsedData.parseData(data)
        val arrayAdapter = CustomAdapter(this@MainActivity, R.layout.oneview, parsedData.appData)
        refreshLayout.isRefreshing = false
        dataList.adapter = arrayAdapter
    }



        private fun downloadXML(s: String?): String {
            var data = ""
            try {
                data = URL(s).readText()
            } catch (e: Exception) {
                when (e) {
                    is MalformedURLException -> Log.e("TAG", "downloadXML: ${e.message}")
                    is IOException -> Log.e("TAG", "downloadXML: ${e.message}")
                    else -> {
                        e.printStackTrace()
                        Log.e("TAG", "downloadXML: ${e.message}")
                    }
                }
            }
            return data
        }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.apple_menus, menu)
        if (limit == 10) {
            menu?.findItem(R.id.tp10)?.isChecked = true
        } else {
            menu?.findItem(R.id.tp10)?.isChecked = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tvfree -> urlToDownload =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.vpaid -> urlToDownload =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.tvsongs -> urlToDownload =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.tp25, R.id.tp10 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    limit = 35 - limit
                } else {
                    Log.d("TAG", "Limit Unchanged")
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        CoroutineScope(IO).launch {
            this@MainActivity.makeDownload(urlToDownload.format(limit))

        }
        return true

    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("limit", limit)
        outState.putString("link", urlToDownload.format(limit))
        super.onSaveInstanceState(outState)
    }


    private suspend fun makeDownload(url: String) {
        if (url != cachedUrl) {
            internetDownload(url)
            Log.e("TAG", "makeDownload: Make Download Called")
            cachedUrl = url
        }
    }
}
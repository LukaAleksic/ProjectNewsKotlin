package com.example.readnews.readnews

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.example.readnews.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_read_news_viewer.*
import kotlinx.android.synthetic.main.fragment_read_news.*


class ReadNewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_news_viewer)
        setSupportActionBar(toolbar)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            navigationToHeadLines()
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_top_headlines -> {
                navigationToHeadLines()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun navigationToHeadLines(){
        val fragment = ReadNewsFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
            .commit()
    }
}

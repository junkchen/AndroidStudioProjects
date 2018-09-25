package com.junkchen.accessibilitydemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myToolbar = findViewById<Toolbar?>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        myToolbar!!.inflateMenu(R.menu.main_menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item!!.itemId) {
        R.id.action_favorite -> {
            showMsg("favorite clicked.")
            true
        }
        R.id.action_setting -> {
            showMsg("setting clicked.")
            true
        }
//        R.id.action_share -> {
//            showMsg("share clicked.")
//            true
//        }
        R.id.action_about -> {
            showMsg("about clicked.")
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        return super.onCreateOptionsMenu(menu)
    }

    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun test() {
//        val p = Person("")
    }
}

class Person(val firstName: String, val lastName: String, var age: Int) {
    constructor(firstName: String, lastName: String, age: Int, ser: String, parent: Person) :
            this(firstName, lastName, age) {

    }
    // ...

}


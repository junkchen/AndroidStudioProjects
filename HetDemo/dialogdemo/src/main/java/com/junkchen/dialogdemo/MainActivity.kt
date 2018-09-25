package com.junkchen.dialogdemo

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_dialog.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("Dialog Title")
                    .setMessage("This is a dialog, create this dialog for test.")
                    .setPositiveButton("Ok") { _, _ ->
                        Toast.makeText(this, "Ok clicked.", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        Toast.makeText(this, "Cancel clicked.", Toast.LENGTH_SHORT).show()
                    }
                    .setNeutralButton("Neutral") { _, _ ->
                        Toast.makeText(this, "Neutral clicked.", Toast.LENGTH_SHORT).show()
                    }
                    .create()
                    .show()
        }
    }
}

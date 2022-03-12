package com.dimitarduino.domasno1

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var recnikRecyclerView: RecyclerView
    private lateinit var barajZborEdit : EditText
    private lateinit var makedonskiZbor : EditText
    private lateinit var angliskiZbor : EditText
    private lateinit var dodajBtn : Button
    private lateinit var izbrisiFiltriBtn : Button
    private lateinit var recnik : ArrayList<Zbor>
    private lateinit var recnikAdapter: RecnikAdapter

    //isceznuvanje na keyboard posle dodaden/izmenet zbor
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun popolniPodatociEdit(mkZbor: String, enZbor: String) {
        this.makedonskiZbor.setText(mkZbor)
        this.angliskiZbor.setText(enZbor)
        this.dodajBtn.setText("ЗАЧУВАЈ ПРОМЕНИ")

        this.dodajBtn.setOnClickListener{
            this.izmeniZbor(mkZbor, enZbor)
        }
    }

    fun izmeniZbor(mkZbor: String, enZbor: String) {
        if (mkZbor.isEmpty()) {
            this.makedonskiZbor.setError("Полето е празно")
        } else if (enZbor.isEmpty()) {
            this.angliskiZbor.setError("Полето е празно")
        } else {

            val path = filesDir
            val letDirectory = File(path, "LET")
            letDirectory.mkdirs()
            val file = File(letDirectory, "recnik.txt")
            val tempFile = File(letDirectory, "tempfile.txt")
            val regex = Regex(mkZbor + "\t" + enZbor)

            tempFile.printWriter().use { writer ->
                file.forEachLine { line ->
                    writer.println(
                        when {
                            regex.matches(line) -> this.makedonskiZbor.text.toString() + "\t" + this.angliskiZbor.text.toString()
                            else -> line
                        }
                    )
                }
            }

            check(file.delete() && tempFile.renameTo(file)) { "failed to replace file" }
            hideKeyboard()
            this.resetirajAkcijaForma()
            this.barajZborEdit.setText("")
            this.citajNanovoRecnik()

            Toast.makeText(applicationContext, "Зборот е успешно променет", Toast.LENGTH_SHORT).show()
        }
    }


    fun resetirajAkcijaForma() {
        this.makedonskiZbor.setText("")
        this.angliskiZbor.setText("")
        this.dodajBtn.setText("ДОДАЈ НОВ ЗБОР")
        this.dodajBtn.setOnClickListener{
            dodajNovZbor()
        }
    }

    fun dodajNovZbor() {
        val mkZbor = this.makedonskiZbor.text.toString()
        val enZbor = this.angliskiZbor.text.toString()

        if (mkZbor.length == 0) {
            this.makedonskiZbor.setError("Полето е празно")
        } else if (enZbor.length == 0) {
            this.angliskiZbor.setError("Полето е празно")
        } else {
            val path = filesDir
            val letDirectory = File(path, "LET")
            letDirectory.mkdirs()
            val file = File(letDirectory, "recnik.txt")

            file.appendText(mkZbor + "\t" + enZbor + "\n")
            val zborNov = Zbor(mkZbor, enZbor)
            recnik.add(zborNov)
            this.makedonskiZbor.setText("")
            this.angliskiZbor.setText("")

            this.makedonskiZbor.clearFocus()
            this.angliskiZbor.clearFocus()
            hideKeyboard()
            this.barajZborEdit.setText("")
            this.recnikAdapter.notifyDataSetChanged()

            Toast.makeText(applicationContext, "Зборот е успешно додаден", Toast.LENGTH_SHORT).show()
        }
    }

    fun citajNanovoRecnik() {
        val path = filesDir
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "recnik.txt")

        recnik.clear()

        file.forEachLine {
            val linija = it.split("\t")
            val zborLinija = Zbor(linija[0], linija[1])
            recnik.add(zborLinija)
        }


        recnikAdapter.notifyDataSetChanged()
    }

    fun barajZborovi(barajVrednost: CharSequence) {
        val path = filesDir
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "recnik.txt")
        recnik.clear()
        file.forEachLine {
            val linija = it.split("\t")
            val zborLinija = Zbor(linija[0], linija[1])

            if (linija[0].contains(barajVrednost)) {
                recnik.add(zborLinija)
            } else {
            }
        }

        this.recnikAdapter.notifyDataSetChanged()
//        this.citajNanovoRecnik()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makedonskiZbor = findViewById(R.id.makedonskiZbor)
        angliskiZbor = findViewById(R.id.angliskiZbor)
        dodajBtn = findViewById(R.id.btn_dodajZbor)
        izbrisiFiltriBtn = findViewById(R.id.izbrisiFiltri)
        barajZborEdit = findViewById(R.id.inputSearch)
        recnikRecyclerView = findViewById(R.id.recnikRecyclerView)

        recnik = ArrayList()

        recnikAdapter = RecnikAdapter(this, recnik)
        recnikRecyclerView.layoutManager = LinearLayoutManager(this)
        recnikRecyclerView.adapter = recnikAdapter
        val path = filesDir
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()

        this.citajNanovoRecnik()

        dodajBtn.setOnClickListener {
            this.dodajNovZbor()
            hideKeyboard()
        }

        izbrisiFiltriBtn.setOnClickListener {
            this.barajZborEdit.setText("")
            hideKeyboard()
        }

        barajZborEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                barajZborovi(s)
            }
        })
    }
}
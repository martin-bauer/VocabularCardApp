package de.martin.vocabularcardapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opencsv.CSVReader
import de.martin.vocabularcardapp.R
import de.martin.vocabularcardapp.VocabSheet
import de.martin.vocabularcardapp.checkStoragePermission
import de.martin.vocabularcardapp.requestStoragePermission
import java.io.IOException
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Paths


class MainActivity : AppCompatActivity() {
    private var hasPermission = false
    private var recyclerView: RecyclerView? = null
    private var textView: TextView? = null
    private var buttonView: Button? = null
    private var vocabAdapter: VocabAdapter? = null
    private var toast: Toast? = null


    // TODO make path variable
    // TODO create difficulty logic & save value as SP?
    // TODO switch startActivityForResult for registerForActivityResult
    // TODO replace Toast with Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        textView = findViewById(R.id.textView)
        buttonView = findViewById(R.id.button)

        val swipeController = SwipeCallback(object : SwipeActions() {
            override fun onRightSwiped(position: Int) {
                removeItem(position, "Difficult")
            }

            override fun onLeftSwiped(position: Int) {
                removeItem(position, "Easy")
            }
        })

        val itemTouchHelper = ItemTouchHelper(swipeController)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            vocabAdapter = VocabAdapter()
            adapter = vocabAdapter
        }
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun removeItem(position: Int, text: String) {
        if (position != -1) {
            toast?.cancel()
//            val snackbar = Snackbar
//                .make(coordinatorLayout, "www.journaldev.com", Snackbar.LENGTH_LONG)
//            snackbar.show()
            toast = Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT)
            val t1 = toast
            t1?.show()
            vocabAdapter?.remove(position)
        }
    }

    override fun onResume() {
        super.onResume()
        hasPermission = checkStoragePermission(this)

        Log.i("Mine", "HasPermission? $hasPermission")
        if (hasPermission) {
            textView?.text = getString(R.string.basic_text_textview)
            buttonView?.text = getString(R.string.basic_text_button)
            val buttonClickListener = View.OnClickListener {
                val intent = Intent()
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select a csv file"), 111)
            }
            findViewById<Button>(R.id.button).setOnClickListener(buttonClickListener)
        } else {
            textView?.text = getString(R.string.no_permission_textview)
            buttonView?.text = getString(R.string.permission_textview)
            val buttonPermissionClickListener = View.OnClickListener {
                requestStoragePermission(this)
            }
            findViewById<Button>(R.id.button).setOnClickListener(buttonPermissionClickListener)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data?.path
            val vocabList: MutableList<VocabSheet> = mutableListOf()

            try {
                val csvPath: String =
                    Environment.getExternalStorageDirectory().absolutePath + selectedFile
                val csvPath2: String = selectedFile.toString()
                Log.i("Mine", "csvPath: $csvPath")
                Log.i("Mine", "csvPath2: $csvPath2")
                val path = "/sdcard/Download/language_new.csv" //static, change this
//                val path = "/Documents/document/language_new.csv" //static, change this
                val reader: Reader = Files.newBufferedReader(Paths.get(path))
                val csvReader = CSVReader(reader)
                val allRecords = csvReader.readAll()
                val iterator: Iterator<Array<String>> = allRecords.iterator()
                while (iterator.hasNext()) {
                    val record = iterator.next()

                    if (record[0] != "") {
                        val vocabSheet = VocabSheet(
                            firstVocab = record[0],
                            secondVocab = record[1],
                            thirdVocab = record[2]
                        )
                        vocabList.add(vocabSheet)
                    }
                }
                csvReader.close()
            } catch (e: IOException) {
                Log.i("Mine", e.toString())
                e.printStackTrace()
            }
            Log.i("Mine", "listSize: " + vocabList.size)
            buttonView?.visibility = View.GONE
            textView?.visibility = View.GONE
            vocabList.shuffle()
            vocabAdapter?.submitList(vocabList)
        }

    }

    //Instead of onActivityResult() method use this one
//    var someActivityResultLauncher = registerForActivityResult(
//        StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
    // Here, no request code
//            val data = result.data?.data?.path
//        }
//    }
}
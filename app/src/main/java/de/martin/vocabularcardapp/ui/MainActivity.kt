package de.martin.vocabularcardapp.ui

import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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
    private var recyclerView: RecyclerView? = null
    private var textView: TextView? = null
    private var buttonView: Button? = null
    private var constraintLayout: ConstraintLayout? = null
    private var vocabAdapter: VocabAdapter? = null

    // TODO make path variable
    // TODO make button texts dynamic
    // TODO create difficulty logic & save value as SP?
    // TODO switch startActivityForResult for registerForActivityResult
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        constraintLayout = findViewById(R.id.constraintLayout)
        textView = findViewById(R.id.textView)
        buttonView = findViewById(R.id.button)
        setupRecyclerview()
    }

    private fun setupRecyclerview() {
        val swipeCallback = setupSwipeCallback()
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            vocabAdapter = VocabAdapter()
            adapter = vocabAdapter
        }
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupSwipeCallback() = SwipeCallback(object : SwipeActions() {
        override fun onRightSwiped(position: Int) {
            removeItem(position, "Difficult")
        }

        override fun onLeftSwiped(position: Int) {
            removeItem(position, "Easy")
        }
    })

    private fun removeItem(position: Int, text: String) {
        if (position != -1) {
            makeSnackbarMessage(text)
            doVibrationEffect()
            vocabAdapter?.remove(position)
        }
    }

    private fun doVibrationEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val ve: VibrationEffect =
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            val cv: CombinedVibration = CombinedVibration.createParallel(ve)
            vm.vibrate(cv)
        } else {
            val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
            val ve: VibrationEffect = VibrationEffect.createOneShot(300, 100)
            v.vibrate(ve)
        }
    }

    private fun makeSnackbarMessage(text: String) {
        constraintLayout?.let {
            val s = Snackbar.make(it, text, Snackbar.LENGTH_SHORT)
            val snackBarView: View = s.view
            snackBarView.setBackgroundColor(resources.getColor(R.color.orange_100))
            s
        }?.show()
    }

    override fun onResume() {
        super.onResume()
        Log.i("Mine", "HasPermission? " + checkStoragePermission(this))
        if (checkStoragePermission(this)) {
            givenStoragePermission()
        } else {
            missingStoragePermission()
        }
    }

    private fun givenStoragePermission() {
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
    }

    private fun missingStoragePermission() {
        textView?.text = getString(R.string.no_permission_textview)
        buttonView?.text = getString(R.string.permission_textview)
        val buttonPermissionClickListener = View.OnClickListener {
            requestStoragePermission(this)
        }
        findViewById<Button>(R.id.button).setOnClickListener(buttonPermissionClickListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            csvFound(data)
        }

    }

    private fun csvFound(data: Intent?) {
        val vocabList: MutableList<VocabSheet> = mutableListOf()
        try {
            val selectedFile = data?.data?.path
            val csvPath: String = selectedFile.toString()
            Log.i("Mine", "csvPath: $csvPath")
            val path = Environment.getExternalStorageDirectory().path+"/Download/language_new.csv" //static, change this
            // val path = "/Documents/document/language_new.csv"
            readCsv(path, vocabList)
            csvSuccessfullyRead(vocabList)
        } catch (e: IOException) {
            Log.i("Mine", e.toString())
            e.printStackTrace()
        }
    }

    private fun readCsv(
        path: String,
        vocabList: MutableList<VocabSheet>
    ) {
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
    }

    private fun csvSuccessfullyRead(vocabList: MutableList<VocabSheet>) {
        Log.i("Mine", "listSize: " + vocabList.size)
        buttonView?.visibility = View.GONE
        textView?.visibility = View.GONE
        vocabList.shuffle()
        vocabAdapter?.submitList(vocabList)
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
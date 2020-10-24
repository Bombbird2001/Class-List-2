package bombbird.com.classlist2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_new_list.*
import kotlinx.android.synthetic.main.activity_new_list.view.*
import java.io.File
import java.lang.StringBuilder

class NewListActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var listOk = false
    private var classOk = false
    private var otherLists: ArrayList<String>? = ArrayList()

    companion object {
        private const val RC_NEW_CLASS = 7777
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_list)

        otherLists = intent.getStringArrayListExtra("otherLists")

        loadSpinner()
        loadButton()

        listInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("RestrictedApi")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    when {
                        s.isEmpty() -> {
                            listOk = false
                            listInputLayout.error = null
                        }
                        s.length >= 30 -> {
                            listOk = false
                            listInputLayout.error = resources.getString(R.string.text_too_long)
                        }
                        else -> {
                            listOk = true
                            listInputLayout.error = null
                        }
                    }
                    otherLists?.contains(s.toString()).let {
                        if (it != null && it) {
                            listOk = false
                            listInputLayout.error = resources.getString(R.string.name_exists)
                        }
                    }
                    updateFabVisibility()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            @Suppress("UNCHECKED_CAST")
            if (requestCode == RC_NEW_CLASS) {
                (spinner3.adapter as ArrayAdapter<String>).remove(resources.getString(R.string.spinner_add_class))
                (spinner3.adapter as ArrayAdapter<String>).add(data?.getStringExtra("newClass"))
                (spinner3.adapter as ArrayAdapter<String>).add(resources.getString(R.string.spinner_add_class))
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == RC_NEW_CLASS) {
                spinner3.setSelection(0)
            }
        }
        classOk = true
        updateFabVisibility()
    }

    private fun loadButton() {
        fab_confirmAddList.setOnClickListener {
            saveNewList()
            finish()
        }
    }

    private fun loadSpinner() {
        val classes = ArrayList<String>()
        classes.add(resources.getString(R.string.spinner_select_class))
        for (file: File in FileHandler.listFilesInDirectory("classes", this)) {
            classes.add(file.name)
        }
        classes.add(resources.getString(R.string.spinner_add_class))
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner3.adapter = dataAdapter
        spinner3.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        classOk = false
        updateFabVisibility()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (spinner3.selectedItem.toString() == resources.getString(R.string.spinner_add_class)) {
            classOk = false
            val intent = Intent(this, EditClassActivity::class.java)
            startActivityForResult(intent, RC_NEW_CLASS)
        } else {
            classOk = spinner3.selectedItem.toString() != resources.getString(R.string.spinner_select_class)
        }
        updateFabVisibility()
    }

    private fun updateFabVisibility() {
        if (classOk && listOk) {
            fab_confirmAddList.show()
        } else {
            fab_confirmAddList.hide()
        }
    }

    private fun saveNewList() {
        val stringBuilder = StringBuilder()
        stringBuilder.append("${spinner3.selectedItem}\n")
        val file = FileHandler.loadFile("classes/${spinner3.selectedItem}", this)
        if (!file.exists()) {
            ToastManager.toastLoadFail(this)
            return
        }
        file.forEachLine { student ->
            if (student.isNotEmpty()) {
                stringBuilder.append("$student\n0\n")
            }
        }
        FileHandler.saveListFile(listInputLayout.listInput.text.toString(), stringBuilder.toString(), this)
    }
}

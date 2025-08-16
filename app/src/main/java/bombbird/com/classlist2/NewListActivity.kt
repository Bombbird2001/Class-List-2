package bombbird.com.classlist2

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import bombbird.com.classlist2.databinding.ActivityNewListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.lang.StringBuilder

class NewListActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var listOk = false
    private var classOk = false
    private var otherLists: ArrayList<String>? = ArrayList()

    private lateinit var binding: ActivityNewListBinding
    private lateinit var listInputLayout: TextInputLayout
    private lateinit var fabConfirmAddList: FloatingActionButton
    private lateinit var spinner3: Spinner
    private lateinit var listInput: TextInputEditText

    private val newClassLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        @Suppress("UNCHECKED_CAST")
        if (result.resultCode == RESULT_OK) {
            (spinner3.adapter as ArrayAdapter<String>).remove(resources.getString(R.string.spinner_add_class))
            (spinner3.adapter as ArrayAdapter<String>).add(result.data?.getStringExtra("newClass"))
            (spinner3.adapter as ArrayAdapter<String>).add(resources.getString(R.string.spinner_add_class))
        } else if (result.resultCode == RESULT_CANCELED) {
            spinner3.setSelection(0)
        }
        classOk = true
        updateFabVisibility()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        listInputLayout = binding.listInputLayout
        fabConfirmAddList = binding.fabConfirmAddList
        spinner3 = binding.spinner3
        listInput = binding.listInput

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

    private fun loadButton() {
        fabConfirmAddList.setOnClickListener {
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
            newClassLauncher.launch(intent)
        } else {
            classOk = spinner3.selectedItem.toString() != resources.getString(R.string.spinner_select_class)
        }
        updateFabVisibility()
    }

    private fun updateFabVisibility() {
        if (classOk && listOk) {
            fabConfirmAddList.show()
        } else {
            fabConfirmAddList.hide()
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
        FileHandler.saveListFile(listInput.text.toString(), stringBuilder.toString(), this)
    }
}

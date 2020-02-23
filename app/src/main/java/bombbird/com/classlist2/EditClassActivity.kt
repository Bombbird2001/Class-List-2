package bombbird.com.classlist2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.NumberPicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_edit_class.*
import kotlinx.android.synthetic.main.activity_edit_class.view.*
import java.io.File
import java.lang.StringBuilder

class EditClassActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var students: ArrayList<String> = ArrayList()
    private var otherClasses: ArrayList<String> = ArrayList()

    private var classOk: Boolean = false
    private var arrayOk: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)

        loadStudents()
        loadRecycler()

        if (intent.hasExtra("otherClasses")) {
            otherClasses = intent.getStringArrayListExtra("otherClasses")
        } else {
            for (file: File in FileHandler.listFilesInDirectory("classes", this)) {
                otherClasses.add(file.name)
            }
        }

        classInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("RestrictedApi")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    when {
                        s.isEmpty() -> {
                            classOk = false
                            classInputLayout.error = null
                        }
                        s.length >= 30 -> {
                            classOk = false
                            classInputLayout.error = resources.getString(R.string.text_too_long)
                        }
                        else -> {
                            classOk = true
                            classInputLayout.error = null
                        }
                    }
                    if (otherClasses.contains(s.toString())) {
                        classOk = false
                        classInputLayout.error = resources.getString(R.string.name_exists)
                    }
                    if (s.toString() == resources.getString(R.string.spinner_add_class) || s.toString() == resources.getString(R.string.spinner_select_class)) {
                        classOk = false
                        classInputLayout.error = resources.getString(R.string.invalid_name)
                    }
                    updateFabVisibility()
                }
            }
        })

        loadButtons()
        initFabCheck()
    }

    fun updateArray(students: ArrayList<String>) {
        this.students = students
        arrayOk = checkStudents()
        updateFabVisibility()
    }

    @SuppressLint("RestrictedApi")
    private fun updateFabVisibility() {
        if (classOk && arrayOk) {
            fab_confirmAddClass.show()
        } else {
            fab_confirmAddClass.hide()
        }
    }

    private fun initFabCheck() {
        arrayOk = checkStudents()
        if (classInput.text?.isNotEmpty()!! && classInput.text?.length!! < 30) {
            classOk = true
        }
        updateFabVisibility()
    }

    private fun checkStudents(): Boolean {
        var ok = false
        for (s in students) {
            if (s.isNotEmpty()) {
                ok = true
            }
        }
        return ok && (students.toHashSet().size == students.size) //Ensure no duplicate names
    }

    private fun loadStudents() {
        students.add("")
        if (intent.hasExtra("className")) {
            val className = intent.getStringExtra("className")
            classInput.setText(className)
            val file = FileHandler.loadFile("classes/$className", this)
            if (!file.exists()) {
                ToastManager.toastLoadFail(this)
                return
            }
            students.clear()
            file.forEachLine { student ->
                if (student.isNotEmpty()) {
                    students.add(student)
                }
            }
        }
    }

    private fun loadRecycler() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = StudentRecyclerAdapter(students, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        (viewAdapter as StudentRecyclerAdapter).recyclerView = recyclerView
    }

    @SuppressLint("RestrictedApi")
    private fun loadButtons() {
        button2.setOnClickListener {
            val builder: AlertDialog.Builder = let {
                AlertDialog.Builder(it)
            }
            builder.setTitle(R.string.dialog_resize)
            builder.setMessage("\nChange class size to:")
            val dialogView = layoutInflater.inflate(R.layout.dialog_number_picker, findViewById(R.id.constraintLayout1), false)
            builder.setView(dialogView)
            val np = dialogView.findViewById<NumberPicker>(R.id.numberPicker1)
            np.minValue = 1
            np.maxValue = 100
            np.value = students.size
            np.wrapSelectorWheel = false
            np.setOnValueChangedListener { _, _, _ -> }
            builder.setPositiveButton(R.string.dialog_set) { _, _ ->
                (viewAdapter as StudentRecyclerAdapter).resizeTo(np.value)
            }
            builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
            builder.show()
        }

        fab_confirmAddClass.setOnClickListener {
            if (intent.hasExtra("className") && classInputLayout.classInput.text.toString() != intent.getStringExtra("className")) {
                val newName = classInputLayout.classInput.text.toString()
                val oldName = intent.getStringExtra("className")
                val builder: AlertDialog.Builder = let {
                    AlertDialog.Builder(it)
                }
                builder.setPositiveButton(R.string.dialog_delete) { _, _ ->
                    FileHandler.deleteClassFile(oldName, this)
                    saveClass()
                }
                builder.setNegativeButton(R.string.dialog_keep) { _, _ ->
                    saveClass()
                }
                builder.setNeutralButton(R.string.dialog_cancel) { _, _ -> }
                builder.setMessage("Class has been renamed from $oldName to $newName, delete $oldName?")
                builder.show()
            } else {
                saveClass()
            }
        }

        if (intent.hasExtra("className")) {
            fab_deleteClass.setOnClickListener {
                val builder: AlertDialog.Builder = let {
                    AlertDialog.Builder(it)
                }
                builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
                    FileHandler.deleteClassFile(intent.getStringExtra("className"), this)
                    finish()
                }
                builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                builder.setMessage("Delete class " + intent.getStringExtra("className") + "?")
                builder.show()
            }
            fab_deleteClass.visibility = View.VISIBLE
        }
    }

    private fun saveClass() {
        val sb = StringBuilder()
        for (student: String in students) {
            if ("" != student) {
                sb.append(student + "\n")
            }
        }
        FileHandler.saveClassFile(classInputLayout.classInput.text.toString(), sb.toString(), this)
        val intent = Intent()
        intent.putExtra("newClass", classInputLayout.classInput.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
